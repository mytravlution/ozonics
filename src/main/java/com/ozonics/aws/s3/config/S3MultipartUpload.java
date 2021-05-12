package com.ozonics.aws.s3.config;


import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.internal.Constants;
import com.amazonaws.services.s3.model.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class S3MultipartUpload {

	
	private static final int AWAIT_TIME = 2; // in seconds
    private static final int DEFAULT_THREAD_COUNT = 4;

    private final String destBucketName;
    private final String filename;

    private final ThreadPoolExecutor executorService;
    private final AmazonS3 s3Client;

    private String uploadId;

    // uploadPartId should be between 1 to 10000 inclusively
    private final AtomicInteger uploadPartId = new AtomicInteger(0);

    private final List<Future<PartETag>> futuresPartETags = new ArrayList<Future<PartETag>>();
    
    public S3MultipartUpload(String destBucketName, String filename, AmazonS3 s3Client) {
        this.executorService = (ThreadPoolExecutor) Executors.newFixedThreadPool(DEFAULT_THREAD_COUNT);
        this.destBucketName = destBucketName;
        this.filename = filename;
        this.s3Client = s3Client;
        
        
    }
    public boolean initializeUpload() {

        InitiateMultipartUploadRequest initRequest = new InitiateMultipartUploadRequest(destBucketName, filename);
        initRequest.setObjectMetadata(getObjectMetadata()); // if we want to set object metadata in S3 bucket
        initRequest.setTagging(getObjectTagging()); // if we want to set object tags in S3 bucket

        uploadId = s3Client.initiateMultipartUpload(initRequest).getUploadId();

        return false;
    }
    
    public void uploadPartAsync(ByteArrayInputStream inputStream) {
        submitTaskForUploading(inputStream, false);
    }
    public void uploadFinalPartAsync(ByteArrayInputStream inputStream) {
        try {
            submitTaskForUploading(inputStream, true);

            // wait and get all PartETags from ExecutorService and submit it in CompleteMultipartUploadRequest
            List<PartETag> partETags = new ArrayList<PartETag>();
            for (Future<PartETag> partETagFuture : futuresPartETags) {
                partETags.add(partETagFuture.get());
            }

            // Complete the multipart upload
            CompleteMultipartUploadRequest completeRequest = new CompleteMultipartUploadRequest(destBucketName, filename, uploadId, partETags);
            s3Client.completeMultipartUpload(completeRequest);

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        } finally {
            // finally close the executor service
            this.shutdownAndAwaitTermination();
        }

    }
    
    private void shutdownAndAwaitTermination() {
    	System.out.println("executor service await and shutdown");
//        log.debug("executor service await and shutdown");
        this.executorService.shutdown();
        try {
            this.executorService.awaitTermination(AWAIT_TIME, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
        	System.out.println("Interrupted while awaiting ThreadPoolExecutor to shutdown");
//            log.debug("Interrupted while awaiting ThreadPoolExecutor to shutdown");
        }
        this.executorService.shutdownNow();
    }
    
    private void submitTaskForUploading(final ByteArrayInputStream inputStream, final boolean isFinalPart) {
        if (uploadId == null || uploadId.isEmpty()) {
            throw new IllegalStateException("Initial Multipart Upload Request has not been set.");
        }

        if (destBucketName == null || destBucketName.isEmpty()) {
            throw new IllegalStateException("Destination bucket has not been set.");
        }

        if (filename == null || filename.isEmpty()) {
            throw new IllegalStateException("Uploading file name has not been set.");
        }

        submitTaskToExecutorService(new Callable<PartETag>() {
			public PartETag call() throws Exception {
			    int eachPartId = uploadPartId.incrementAndGet();
			    UploadPartRequest uploadRequest = new UploadPartRequest()
			            .withBucketName(destBucketName)
			            .withKey(filename)
			            .withUploadId(uploadId)
			            .withPartNumber(eachPartId) // partNumber should be between 1 and 10000 inclusively
			            .withPartSize(inputStream.available())
			            .withInputStream(inputStream);

			    if (isFinalPart) {
			        uploadRequest.withLastPart(true);
			    }
System.out.println(String.format("Submitting uploadPartId: "+eachPartId+" of partSize: "+inputStream.available()+""));
//			    log.info(String.format("Submitting uploadPartId: %d of partSize: %d", eachPartId, inputStream.available()));

			    UploadPartResult uploadResult = s3Client.uploadPart(uploadRequest);
System.out.println("Successfully submitted uploadPartId: "+eachPartId+"");
//			    log.info(String.format("Successfully submitted uploadPartId: %d", eachPartId));
			    return uploadResult.getPartETag();
			}
		});
    }

    private void submitTaskToExecutorService(Callable<PartETag> callable) {
        // we are submitting each part in executor service and it does not matter which part gets upload first
        // because in each part we have assigned PartNumber from "uploadPartId.incrementAndGet()"
        // and S3 will accumulate file by using PartNumber order after CompleteMultipartUploadRequest
        Future<PartETag> partETagFuture = this.executorService.submit(callable);
        this.futuresPartETags.add(partETagFuture);
    }
    
    
    private ObjectTagging getObjectTagging() {
        // create tags list for uploading file
    	ObjectTagging obj = new ObjectTagging(new ArrayList<Tag>());
        return obj;
    }

    private ObjectMetadata getObjectMetadata() {
        // create metadata for uploading file
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentType("application/zip");
        return objectMetadata;
    }

}
