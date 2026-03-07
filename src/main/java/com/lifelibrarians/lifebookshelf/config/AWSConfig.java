package com.lifelibrarians.lifebookshelf.config;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.lambda.AWSLambda;
import com.amazonaws.services.lambda.AWSLambdaClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ses.SesClient;


@Configuration
public class AWSConfig {

	@Value("${cloud.aws.s3.endpoint}")
	private String endpoint;
	@Value("${cloud.aws.credentials.access-key}")
	private String accessKey;
	@Value("${cloud.aws.credentials.secret-key}")
	private String secretKey;
	@Value("${cloud.aws.region.static}")
	private String region;

	@Bean
	public AmazonS3Client amazonS3Client() {
		AwsClientBuilder.EndpointConfiguration endpointConfiguration =
				new AwsClientBuilder.EndpointConfiguration(endpoint, region);

		return (AmazonS3Client) AmazonS3Client.builder()
				.withEndpointConfiguration(endpointConfiguration)
				.withPathStyleAccessEnabled(true)
				.withCredentials(
						new AWSStaticCredentialsProvider(new BasicAWSCredentials(accessKey, secretKey)))
				.build();
	}

	@Bean
	public AWSLambda awsLambda() {
		return AWSLambdaClientBuilder.standard()
				.withRegion(region)
				.withCredentials(
						new AWSStaticCredentialsProvider(new BasicAWSCredentials(accessKey, secretKey)))
				.build();
	}

	@Bean
	public SesClient sesClient() {
		return SesClient.builder()
				.region(Region.of(region))
				.credentialsProvider(StaticCredentialsProvider.create(
						AwsBasicCredentials.create(accessKey, secretKey)))
				.build();
	}
}
