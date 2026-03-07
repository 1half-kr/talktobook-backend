#!/bin/bash

# S3 버킷 생성
awslocal --region us-east-1 s3 mb s3://lifebookshelf-image-bucket

# 필요한 경로 생성
awslocal --region us-east-1 s3api put-object --bucket lifebookshelf-image-bucket --key profile-images/
awslocal --region us-east-1 s3api put-object --bucket lifebookshelf-image-bucket --key bio-cover-images/
awslocal --region us-east-1 s3api put-object --bucket lifebookshelf-image-bucket --key book-cover-images/
