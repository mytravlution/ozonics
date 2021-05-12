package com.ozonics.aws.s3.serv;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Service;

import com.amazonaws.HttpMethod;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.util.IOUtils;

@Service
public class AmazonClient {

//	private AmazonS3 s3client;
	ResourceBundle resource = ResourceBundle.getBundle("resources/web");
	
	private String bucketName = resource.getString("bucketName");
	private String accessKey= resource.getString("accessKey");
	private String secretKey=resource.getString("secretKey");
	

	@PostConstruct
	public void uploadFileTos3bucket(String fileName, File file, String ext) {
		AWSCredentials credentials = new BasicAWSCredentials(this.accessKey, this.secretKey);
		AmazonS3 s3client= AmazonS3ClientBuilder
		         .standard()
		         .withRegion(Regions.US_EAST_2)
		         .withCredentials(new AWSStaticCredentialsProvider(credentials))
		         .build();
		System.out.println("yessssss");
		System.out.println(bucketName+ext);
		s3client.putObject(
				new PutObjectRequest(bucketName+ext, fileName, file));
				//.withCannedAcl(CannedAccessControlList.PublicRead));
		System.out.println("File uploaded");
	}

	//to download file
	@PostConstruct
	public byte[] downloadFileFrolS3bucket(String fileName, String ext) {
		AWSCredentials credentials = new BasicAWSCredentials(this.accessKey, this.secretKey);
		AmazonS3 s3client= AmazonS3ClientBuilder
		         .standard()
		         .withRegion(Regions.US_EAST_2)
		         .withCredentials(new AWSStaticCredentialsProvider(credentials))
		         .build();
		System.out.println("yessssss");
		System.out.println(bucketName+ext);

		 byte[] content = null;
	        final S3Object s3Object = s3client.getObject(bucketName+ext, fileName);
	        final S3ObjectInputStream stream = s3Object.getObjectContent();
	        try {
	            content = IOUtils.toByteArray(stream);
	            s3Object.close();
	            System.out.println("File Sent");
	        } catch(final IOException ex) {
	        }
	        return content;
	}
	//to delete file
	@PostConstruct
	public int deleteFileFromS3bucket(String fileName, String ext) {
		AWSCredentials credentials = new BasicAWSCredentials(this.accessKey, this.secretKey);
		AmazonS3 s3client= AmazonS3ClientBuilder
		         .standard()
		         .withRegion(Regions.US_EAST_2)
		         .withCredentials(new AWSStaticCredentialsProvider(credentials))
		         .build();
		System.out.println("yessssss");
		System.out.println(bucketName+ext);
		int status =0;
		try {
		  final DeleteObjectRequest deleteObjectRequest = new DeleteObjectRequest(bucketName+ext, fileName);
	        s3client.deleteObject(deleteObjectRequest);
	        status =1;
		}catch(Exception e) {
			e.printStackTrace();
		}
		return status;
	}
	
	
}
