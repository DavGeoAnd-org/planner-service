package com.davgeoand.api;

import com.davgeoand.api.helper.ServiceProperties;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ServiceRunner {
    public static void main(String[] args) {
        ServiceProperties.init("service.properties", "build.properties");
        new JavalinService().start();
    }
}