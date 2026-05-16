package com.Jarvis.Jarvis;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class JarvisApplication {

	public static void main(String[] args) {
		nu.pattern.OpenCV.loadLocally();
		System.out.println("OpenCV loaded: " + org.opencv.core.Core.VERSION);
		SpringApplication.run(JarvisApplication.class, args);
		System.out.println("🤖 JARVIS is online!");
	}

}
