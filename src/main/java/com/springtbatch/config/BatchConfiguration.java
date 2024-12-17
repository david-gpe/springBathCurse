package com.springtbatch.config;

import com.springtbatch.decider.MyJobExecutionDecider;
import com.springtbatch.domain.*;
import com.springtbatch.exception.MyException;
import com.springtbatch.listener.*;
import com.springtbatch.processor.FilterProductItemProcessor;
import com.springtbatch.processor.TransformProductItemProcessor;
import com.springtbatch.reader.ProductNameItemReader;
import com.springtbatch.skippolicy.MySkipPolicy;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.job.builder.FlowBuilder;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.job.flow.Flow;
import org.springframework.batch.core.job.flow.JobExecutionDecider;
import org.springframework.batch.core.listener.ExecutionContextPromotionListener;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.batch.item.database.support.SqlPagingQueryProviderFactoryBean;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.BeanWrapperFieldExtractor;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.item.support.CompositeItemProcessor;
import org.springframework.batch.item.validator.BeanValidatingItemProcessor;
import org.springframework.batch.item.validator.ValidatingItemProcessor;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

@Configuration
/**Anotacion deprecada
 * @EnableBatchProcessing*/
public class BatchConfiguration {

    /**
     * Clases deprecadas en SpringBatch 5.0.0
     *
     * @Autowired public JobBuilderFactory jobBuilderFactory;
     * @Autowired public StepBuilderFactory stepBuilderFactory;
     */


    @Autowired
    public DataSource dataSource;

    /**
     * Metodo generico para un objeto de tipo producto
     */
    @Bean
    public ItemReader<String> itemReader() {
        List<String> productList = new ArrayList<>();
        productList.add("Product 1");
        productList.add("Product 2");
        productList.add("Product 3");
        productList.add("Product 4");
        productList.add("Product 5");
        productList.add("Product 6");
        productList.add("Product 7");
        productList.add("Product 8");

        return new ProductNameItemReader(productList);
    }

    /**
     * Configuracion para leer informacion de un archivo csv
     * se apolla en la clase ProductFieldSetMapper y Product
     */
    @Bean
    public ItemReader<Product> flatFileItemReader() {
        FlatFileItemReader<Product> itemReader = new FlatFileItemReader<>();
        itemReader.setLinesToSkip(1);
        itemReader.setResource(new ClassPathResource("data/Product_Details.csv"));

        DefaultLineMapper<Product> lineMapper = new DefaultLineMapper<>();

        DelimitedLineTokenizer lineTokenizer = new DelimitedLineTokenizer();
        lineTokenizer.setNames("product_id", "product_name", "product_category", "product_price");

        lineMapper.setLineTokenizer(lineTokenizer);
        lineMapper.setFieldSetMapper(new ProductFieldSetMapper());

        itemReader.setLineMapper(lineMapper);

        return itemReader;
    }

    /**
     * Metodo basico para la obtencion de informacion de una base de datos
     */
    @Bean
    public ItemReader<Product> jdbcCursorItemReader() {
        JdbcCursorItemReader<Product> itemReader = new JdbcCursorItemReader<>();
        itemReader.setDataSource(dataSource);
        itemReader.setSql("select * from product_details order by product_id");
        itemReader.setRowMapper(new ProductRowMapper());

        return itemReader;

    }

    /**
     * Metodo para obtener informacion de una base de datos relacional
     * con querys especificas
     */
    @Bean
    public ItemReader<Product> jdbcPagingItemReader() throws Exception {
        JdbcPagingItemReader<Product> itemReader = new JdbcPagingItemReader<>();
        itemReader.setDataSource(dataSource);

        SqlPagingQueryProviderFactoryBean factory = new SqlPagingQueryProviderFactoryBean();
        factory.setDataSource(dataSource);
        factory.setSelectClause("select product_id, product_name, product_category, product_price");
        factory.setFromClause("from product_details");
        factory.setSortKey("product_id");

        itemReader.setQueryProvider(factory.getObject());
        itemReader.setRowMapper(new ProductRowMapper());
        itemReader.setPageSize(3);

        return itemReader;
    }

    /**
     * Metodo para escribir informacion en un archivo de tipo csv
     */
    @Bean
    public ItemWriter<Product> flatFileItemWriter() {
        FlatFileItemWriter<Product> itemWriter = new FlatFileItemWriter<>();
        itemWriter.setResource(new FileSystemResource("src/main/resources/data/Product_Details_Output.csv"));

        DelimitedLineAggregator<Product> lineAggregator = new DelimitedLineAggregator<>();
        lineAggregator.setDelimiter(",");

        BeanWrapperFieldExtractor<Product> fieldExtractor = new BeanWrapperFieldExtractor<>();
        fieldExtractor.setNames(new String[]{"productId", "productName", "productCategory", "productPrice"});

        lineAggregator.setFieldExtractor(fieldExtractor);
        itemWriter.setLineAggregator(lineAggregator);

        return itemWriter;
    }

    /***/
    @Bean
    public JdbcBatchItemWriter<Product> jdbcFlatItemWriter() {
        JdbcBatchItemWriter<Product> itemWriter = new JdbcBatchItemWriter<>();
        itemWriter.setDataSource(dataSource);

        itemWriter.setSql("insert into product_details_output values(:productId,:productName,:productCategory,:productPrice)");
        /**itemWriter.setItemPreparedStatementSetter(new ProductItemPreparedStatementSetter());*/
        itemWriter.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider());

        return itemWriter;
    }


    @Bean
    public JdbcBatchItemWriter<OSProduct> jdbcBatchItemWriter() {
        JdbcBatchItemWriter<OSProduct> itemWriter = new JdbcBatchItemWriter<>();
        itemWriter.setDataSource(dataSource);
        //itemWriter.setSql("insert into product_details_output values(:productId,:productName,:productCategory,:productPrice, :taxPercent, :sku, :shippingRate)");
        itemWriter.setSql("insert into os_product_details values(:productId,:productName,:productCategory,:productPrice, :taxPercent, :sku, :shippingRate)");
        /**itemWriter.setItemPreparedStatementSetter(new ProductItemPreparedStatementSetter());*/
        itemWriter.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider());

        return itemWriter;
    }

    @Bean
    public ItemProcessor<Product, Product> filterProductItemProcessor() {
        return new FilterProductItemProcessor();
    }

    @Bean
    public ValidatingItemProcessor<Product> validatingItemProcessor() {
        ValidatingItemProcessor<Product> validatingItemProcessor = new ValidatingItemProcessor<>(new ProductValidator());
        validatingItemProcessor.setFilter(true);
        return validatingItemProcessor;
    }

    @Bean
    public BeanValidatingItemProcessor<Product> validateProductItemProcessor() {
        BeanValidatingItemProcessor<Product> beanValidatingItemProcessor = new BeanValidatingItemProcessor<>();
        //beanValidatingItemProcessor.setFilter(true);
        return beanValidatingItemProcessor;
    }

    @Bean
    public MyJobExecutionListener myJobExecutionListener() {
        return new MyJobExecutionListener();

    }

    @Bean
    public MyStepExecutionListener myStepExecutionListener() {
        return new MyStepExecutionListener();
    }


    @Bean
    public JobExecutionDecider decider() {
        return new MyJobExecutionDecider();
    }


    @Bean
    public ItemProcessor<Product, OSProduct> transformProductItemProcessor() {
        return new TransformProductItemProcessor();
    }

    @Bean
    public CompositeItemProcessor<Product, OSProduct> itemProcessor() {
        CompositeItemProcessor<Product, OSProduct> compositeItemProcessor = new CompositeItemProcessor();
        List itemProcessors = new ArrayList();
        itemProcessors.add(validateProductItemProcessor());
        itemProcessors.add(filterProductItemProcessor());
        itemProcessors.add(transformProductItemProcessor());

        compositeItemProcessor.setDelegates(itemProcessors);
        return compositeItemProcessor;
    }

    @Bean
    public MyChunkListener myChunkListener() {
        return new MyChunkListener();
    }

    @Bean
    public MyItemReadListener myItemReadListener() {
        return new MyItemReadListener();
    }

    @Bean
    public MyItemProcessListener myItemProcessListener() {
        return new MyItemProcessListener();
    }

    @Bean
    public MyItemWriteListener myItemWriteListener() {
        return new MyItemWriteListener();
    }

    @Bean
    public MySkipListener mySkipListener(){
        return new MySkipListener();
    }

    @Bean
    public MySkipPolicy mySkipPolicy(){
        return new MySkipPolicy();
    }

    @Bean
    public Step step(JobRepository jobRepository, PlatformTransactionManager platformTransactionManager) throws Exception {
        return /**Forma deprecada de SpringBathc 5.0.0
         this.stepBuilderFactory.get
         Al cambiar de version se modifica la implementacion, requiriendo un segundo parametro
         en este caso jobRepository, el cual recibimos en el step*/
                new StepBuilder("chunkBaseStep1", jobRepository)
                        /**El metodo chunk tambien requiere de un segundo parametro  que es la conexion
                         * a la base de datos*/
                        .</**String*/Product, /**String*//**Product*/OSProduct>chunk(3, platformTransactionManager)
                        .reader(flatFileItemReader())
                        .processor(itemProcessor())
                        .writer(jdbcBatchItemWriter()
                                /**new ItemWriter<Product/**String>() {
                                @Override public void write(List<? extends /**String Product> list) throws Exception {
                                System.out.println("Chunk-processing Started");
                                list.forEach(System.out::println);
                                System.out.println("Chunk-processing Ended");
                                }
                                }*/)
                        .faultTolerant()
                        /**.skip(ValidationException.class)
                        .skip(FlatFileParseException.class)
                        .skipLimit(3)*/
                        /**.skipPolicy(mySkipPolicy())*/
                        .retry(MyException.class)
                        .retryLimit(4)
                        .listener(mySkipListener())
                        .listener(myChunkListener())
                        /**.listener(myItemReadListener())
                        .listener(myItemProcessListener())
                        .listener(myItemWriteListener())*/
                        .build();
        /**return this.stepBuilderFactory.get("step1").tasklet(new Tasklet() {
        @Override public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {
        System.out.println("step1 executed");
        return RepeatStatus.FINISHED;
        }
        }).build();*/
    }

    @Bean
    public Step step1(JobRepository jobRepository, PlatformTransactionManager platformTransactionManager) throws Exception {
        return new StepBuilder("step1", jobRepository)
                .tasklet(new Tasklet() {
                    @Override
                    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
                        System.out.println("step1 executed on thread " + Thread.currentThread().getName());
                        ExecutionContext jobExecutionContext = chunkContext.getStepContext().getStepExecution()
                                .getJobExecution().getExecutionContext();
                        System.out.println("Job Execution Contex: " + jobExecutionContext);
                        //jobExecutionContext.put("sk1", "ABC");
                        ExecutionContext stepExecutionContext = chunkContext.getStepContext().getStepExecution().getExecutionContext();
                        stepExecutionContext.put("sk1", "ABC");
                        return RepeatStatus.FINISHED;
                    }
                }, platformTransactionManager).listener(promotionListener())
                .build();
    }

    @Bean
    public Step step2(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("step2", jobRepository).tasklet(new Tasklet() {
            @Override
            public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {
                System.out.println("step2 executed  on thread " + Thread.currentThread().getName());
                ExecutionContext jobExecutionContext = chunkContext.getStepContext().getStepExecution()
                        .getJobExecution().getExecutionContext();
                System.out.println("Job Execution Contex: " + jobExecutionContext);
                //jobExecutionContext.put("sk2", "KLM");
                ExecutionContext stepExecutionContext = chunkContext.getStepContext().getStepExecution().getExecutionContext();
                stepExecutionContext.put("sk2", "TUV");
                return RepeatStatus.FINISHED;
            }
        }, transactionManager).listener(promotionListener()).build();
    }

    @Bean
    public Step step3(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("step3", jobRepository).tasklet(new Tasklet() {
                    @Override
                    public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {
                        System.out.println("step3 executed on thread " + Thread.currentThread().getName());
                        ExecutionContext jobExecutionContext = chunkContext.getStepContext().getStepExecution()
                                .getJobExecution().getExecutionContext();
                        System.out.println("Job Execution Contex: " + jobExecutionContext);
                        return RepeatStatus.FINISHED;
                    }
                }, transactionManager).listener(myStepExecutionListener())
                .build();
    }

    @Bean
    public Step step4(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("step4", jobRepository).tasklet(new Tasklet() {
            @Override
            public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {
                System.out.println("step4 executed on thread " + Thread.currentThread().getName());
                return RepeatStatus.FINISHED;
            }
        }, transactionManager).build();
    }

    @Bean
    public Step step5(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("step5", jobRepository).tasklet(new Tasklet() {
            @Override
            public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {
                if (false) {
                    throw new Exception("Test Exception");
                }
                System.out.println("step5 executed on thread " + Thread.currentThread().getName());
                return RepeatStatus.FINISHED;
            }
        }, transactionManager).build();
    }

    @Bean
    public Step step6(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("step6", jobRepository).tasklet(new Tasklet() {
            @Override
            public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {
                System.out.println("step6 executed on thread " + Thread.currentThread().getName());
                return RepeatStatus.FINISHED;
            }
        }, transactionManager).build();
    }

    @Bean
    public Step step7(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("step7", jobRepository).tasklet(new Tasklet() {
            @Override
            public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {
                System.out.println("step7 executed on thread " + Thread.currentThread().getName());
                return RepeatStatus.FINISHED;
            }
        }, transactionManager).build();
    }

    @Bean
    public Step step8(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("step8", jobRepository).tasklet(new Tasklet() {
            @Override
            public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {
                System.out.println("step8 executed on thread " + Thread.currentThread().getName());
                return RepeatStatus.FINISHED;
            }
        }, transactionManager).build();
    }

    @Bean
    public Step job3Step(JobRepository jobRepository, Job job3) {
        return new StepBuilder("job3Step", jobRepository).job(job3).build();
    }
    /**@Bean public Step step2() {
    return this.stepBuilderFactory.get("step2").tasklet(new Tasklet() {
    @Override public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {
    boolean isFailure = false;
    if (isFailure) {
    throw new Exception("Test Exception");
    }
    System.out.println("step2 executed");
    return RepeatStatus.FINISHED;
    }
    })/**.listener(myStepExecutionListener())*/
    /**
     * .build();
     * }
     *
     * @Bean public Step step3() {
     * return this.stepBuilderFactory.get("step3").tasklet(new Tasklet() {
     * @Override public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {
     * System.out.println("step3 executed");
     * return RepeatStatus.FINISHED;
     * }
     * }).build();
     * }
     * @Bean public Step step4() {
     * return this.stepBuilderFactory.get("step4").tasklet(new Tasklet() {
     * @Override public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {
     * System.out.println("step4 executed");
     * return RepeatStatus.FINISHED;
     * }
     * }).build();
     * }
     */

    @Bean
    public Flow flow1(Step step3, Step step4) {
        FlowBuilder<Flow> flowBuilder = new FlowBuilder("flow1");
        flowBuilder.start(step3)
                .next(step4)
                .end();
        return flowBuilder.build();
    }

    @Bean
    public Flow flow2(Step step5, Step step6) {
        FlowBuilder<Flow> flowBuilder = new FlowBuilder("flow2");
        flowBuilder.start(step5)
                .next(step6)
                .end();
        return flowBuilder.build();
    }

    @Bean
    public Flow flow3(Step step7, Step step8) {
        FlowBuilder<Flow> flowBuilder = new FlowBuilder("flow3");
        flowBuilder.start(step7)
                .next(step8)
                .end();
        return flowBuilder.build();
    }

    @Bean
    public Flow splitFlow(Flow flow1, Flow flow2, Flow flow3) {
        return new FlowBuilder<Flow>("splitFlow")
                .split(new SimpleAsyncTaskExecutor())
                .add(flow1, flow2, flow3)
                .build();
    }

    @Bean
    public StepExecutionListener promotionListener() {
        ExecutionContextPromotionListener promotionListener = new ExecutionContextPromotionListener();
        promotionListener.setKeys(new String[]{"sk1", "sk2"});
        return promotionListener;
    }

    @Bean
    public Job job(JobRepository jobRepository, Step step) {
        return new JobBuilder("job", jobRepository)
                .start(step)
                .build();

    }

    @Bean
    public Job job1(JobRepository jobRepository, Step step1, Step step2,
                    Step step3, Step step4, Step step5, Flow flow1) {
        return new JobBuilder("job1", jobRepository)
                .listener(myJobExecutionListener())
                .start(step1)
                .next(step2)
                .next(decider())
                    .on("STEP_3").to(step3)
                .from(decider())
                    .on("STEP_4").to(step4)
                .from(decider())
                    .on("STEP_5").to(step5)
                .end()
                /**.on("COMPLETED").to(flow1)
                 .end()*/
                .build();

    }

    /**
     * @Bean public Job job2(JobRepository jobRepository, Step step5, Step step6,
     * Flow flow) {
     * return new JobBuilder("job2", jobRepository)
     * .start(flow)
     * .next(step5)
     * .next(step6)
     * .end()
     * .build();
     * <p>
     * }
     */

    @Bean
    public Job job2(JobRepository jobRepository, Step job3Step, Flow splitFlow) {
        return new JobBuilder("job2", jobRepository)
                .listener(myJobExecutionListener())
                .start(splitFlow)
                //.split(new SimpleAsyncTaskExecutor())
                //.add(flow1, flow2, flow3)
                //.next(job3Step)
                .end()
                .build();

    }

    @Bean
    public Job job3(JobRepository jobRepository, Step step5, Step step6) {
        return new JobBuilder("job3", jobRepository)
                .start(step5)
                .next(step6)
                .build();

    }

    /**@Bean public Job firstJob(JobRepository jobRepository, PlatformTransactionManager transactionManager) throws Exception {
    return /**Esta seccion de la implementacion tambien esta deprecada y se actualiza a la mostrada
    abajo
    this.jobBuilderFactory.get
    esta esperara un segundo metodo que es el JobRepository*/
    /**new JobBuilder("job1", jobRepository)//.preventRestart()
     /**Dado que se modifico el step1 debemos agregar los parametros*/
    /**.start(step1(jobRepository, transactionManager))
     /**.on("COMPLETED").to(decider())
     .on("TEST_STATUS").to(step2())
     .from(decider())
     .on("*").to(step3())
     /***.from(step2())
     .on("TEST_STATUS").to(step3())
     .from(step2())
     .on("*").to(step4())*/
    /**.end()
     /***.next(step2())
     .next(step3())*/
    /** .build();
     }*/
}
