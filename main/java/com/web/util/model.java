package com.web.util;

import java.io.IOException;

import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.modelimport.keras.KerasModelImport;
import org.deeplearning4j.nn.modelimport.keras.exceptions.InvalidKerasConfigurationException;
import org.deeplearning4j.nn.modelimport.keras.exceptions.UnsupportedKerasConfigurationException;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.nd4j.linalg.io.ClassPathResource;

public class model {
	public static MultiLayerNetwork loadModel() {
		System.out.println("===========加载模型============");
		String modelJson;
		try {
			modelJson = new ClassPathResource("model_12.20_gen.json").getFile().getPath();
			try {
				MultiLayerConfiguration modelConfig = KerasModelImport.importKerasSequentialConfiguration(modelJson);
				String modelWeights = new ClassPathResource("model_12.20_gen.h5").getFile().getPath();
				MultiLayerNetwork network = KerasModelImport.importKerasSequentialModelAndWeights(modelJson, modelWeights);
				System.out.println("===========模型加载成功！==========");
				return network;
			} catch (InvalidKerasConfigurationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (UnsupportedKerasConfigurationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;	
	}
}
