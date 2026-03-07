package com.lifelibrarians.lifebookshelf.image.domain;

import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.lifelibrarians.lifebookshelf.exception.ServiceException;
import com.lifelibrarians.lifebookshelf.log.LogLevel;
import com.lifelibrarians.lifebookshelf.log.Logging;
import com.lifelibrarians.lifebookshelf.utils.exception.UtilsExceptionStatus;
import java.util.Date;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
@Logging(level = LogLevel.DEBUG)
public class AwsS3Manager implements
		ObjectResourceManager {

	private final String bucket;
	private final AmazonS3Client amazonS3Client;
	private final Long preSignedUrlExpiration;

	public AwsS3Manager(
			AmazonS3Client amazonS3Client,
			@Value("${cloud.aws.s3.bucket}") String bucket,
			@Value("${cloud.aws.s3.expiration}") Long preSignedUrlExpiration) {
		this.amazonS3Client = amazonS3Client;
		this.bucket = bucket;
		this.preSignedUrlExpiration = preSignedUrlExpiration;
	}

	@Override
	public void upload(MultipartFile multipartFile, String objectKey) {
		ObjectMetadata objectMetadata = createObjectMetadata(multipartFile);
		try {
			PutObjectRequest putObjectRequest = new PutObjectRequest(bucket, objectKey,
					multipartFile.getInputStream(), objectMetadata);
			amazonS3Client.putObject(putObjectRequest);
		} catch (Exception e) {
			throw new ServiceException(UtilsExceptionStatus.STORAGE_UPLOAD_ERROR);
		}
	}

	private ObjectMetadata createObjectMetadata(MultipartFile multipartFile) {
		ObjectMetadata objectMetadata = new ObjectMetadata();

		objectMetadata.setContentType(multipartFile.getContentType());
		objectMetadata.setContentLength(multipartFile.getSize());
		return objectMetadata;
	}

	@Override
	public void delete(String key) {
		try {
			amazonS3Client.deleteObject(bucket, key);
		} catch (Exception e) {
			throw new ServiceException(UtilsExceptionStatus.STORAGE_DELETE_ERROR);
		}
	}

	@Override
	public String getObjectUrl(String key) {
		if (!doesObjectExist(key)) {
			return null;
		}
		return amazonS3Client.getUrl(bucket, key).toString();
	}

	@Override
	public String getPreSignedUrl(String objectKey) {
		GeneratePresignedUrlRequest request = createPreSignedUrlRequest(objectKey);
		return amazonS3Client.generatePresignedUrl(request).toString();
	}

	@Override
	public boolean doesObjectExist(String objectKey) {
		return amazonS3Client.doesObjectExist(bucket, objectKey);
	}

	private GeneratePresignedUrlRequest createPreSignedUrlRequest(String objectKey) {
		return new GeneratePresignedUrlRequest(bucket, objectKey)
				.withMethod(HttpMethod.PUT)
				.withExpiration(getPreSignedUrlExpiration());
	}

	private Date getPreSignedUrlExpiration() {
		Date expriration = new Date();
		long expTimeMillis = expriration.getTime();
		expTimeMillis += this.preSignedUrlExpiration;
		expriration.setTime(expTimeMillis);
		return expriration;
	}
}
