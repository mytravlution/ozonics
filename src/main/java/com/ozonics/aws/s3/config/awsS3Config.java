package com.ozonics.aws.s3.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.google.api.client.util.Value;

@Configuration
public class awsS3Config {
	
	 
	        // Access key id will be read from the application.properties file during the application intialization.
	        @Value("${aws.access_key_id}")
	        private String accessKeyId;
	        // Secret access key will be read from the application.properties file during the application intialization.
	        @Value("${aws.secret_access_key}")
	        private String secretAccessKey;
	        // Region will be read from the application.properties file  during the application intialization.
	        @Value("${aws.s3.region}")
	        private String region;
	     
	        @Bean
	        public AmazonS3 getAmazonS3Cient() {
	            final BasicAWSCredentials basicAWSCredentials = new BasicAWSCredentials(accessKeyId, secretAccessKey);
	            // Get AmazonS3 client and return the s3Client object.
	            return AmazonS3ClientBuilder
	                    .standard()
	                    .withRegion(Regions.fromName(region))
	                    .withCredentials(new AWSStaticCredentialsProvider(basicAWSCredentials))
	                    .build();


//	   @Value("${aws.access_key_id}")
//	    private String accessKeyId;
//	    @Value("${aws.secret_access_key}")
//	    private String secretAccessKey;
//	    @Value("${aws.s3.region}")
//	    private String region;
//	    
//	    public awsS3Config() {
//	    	 this.accessKeyId="AKIATJPYHMGVTA75UL5S";
////	        this.accessKeyId = "AKIA2OA57QFWUBVCVKSW"; // for second test
//////	        this.accessKeyId = "AKIATJPYHMGV2SMRJPHN";   //for test
////	       
//	        this.secretAccessKey = "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";
////	        this.secretAccessKey = "4jLDYcIz72u9iyGPjKfCcogotV+NM+kb3kigSi74"; // for second test
//////	        this.secretAccessKey = "lxBaGamnOPTzq6gpqp1CpmFFnGlPM9p1C0IyJ22f";     //for test
//
//	        this.region = "Asia Pacific (Mumbai)";
//	        
	        
	        //new key: AKIAJXQURPH2VYDTCPZQ
	        // secret: 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

	    }
	    
//	    
//	    @Bean
//	    public AmazonS3 getAmazonS3Cient() {
//	        final BasicAWSCredentials basicAWSCredentials = new BasicAWSCredentials(this.accessKeyId, this.secretAccessKey);
//	        final AmazonS3Client client = new AmazonS3Client((AWSCredentials)basicAWSCredentials);
//	        final List<Bucket> buckets = (List<Bucket>)client.listBuckets();
//	        System.out.println("Your Amazon S3 buckets are:");
//	        for (final Bucket b : buckets) {
//	            System.out.println("* " + b.getName());

	        

}
