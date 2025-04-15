#!/bin/bash

awslocal sqs create-queue --queue-name pix-send-queue
awslocal sqs create-queue --queue-name pix-confirm-queue