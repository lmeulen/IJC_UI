/**
 * Copyright (C) 2016 Leo van der Meulen
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation version 3.0
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * See: http://www.gnu.org/licenses/gpl-3.0.html
 *
 * Problemen in deze code:
 */
package nl.detoren.ijc.neural;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import weka.classifiers.Evaluation;
import weka.classifiers.functions.MultilayerPerceptron;
import weka.core.DenseInstance;
import weka.core.Instances;

public class Voorspeller {
	private final static Logger logger = Logger.getLogger(Voorspeller.class.getName());
	private final static String defaultNetworkFile = "voorspeller.mlp";
	private final static String defaultTrainingFile = "traindata.arff";

	MultilayerPerceptron mlp;

	public Voorspeller() {
		mlp = null;
	}

	/**
	 * Create a multi layer perceptron network, with the following parameters -
	 * learning rate = 0.1 - momentum = 0.1 - training iterations = 4096 -
	 * hidden layers = t (equals number of attributes + number of classes)
	 *
	 * @return
	 */
	public void initialiseerStandaard() {
		// Instance of NN
		mlp = new MultilayerPerceptron();
		// Setting Parameters
		mlp.setLearningRate(0.1);
		mlp.setMomentum(0.1);
		mlp.setTrainingTime(4096);
		mlp.setHiddenLayers("t");
	}

	/**
	 * Initialiseer met opgeslagen netwerk. Standaard bestandsnaam
	 */
	public void initialiseer() {
		initialiseer(defaultNetworkFile);
	}

	/**
	 * Initialiseer met opgeslagen network
	 *
	 * @param networkfile
	 *            Bestandsnaam van bestand met netwerk configuratie
	 */
	public void initialiseer(String networkfile) {
		try {
			mlp = (MultilayerPerceptron) weka.core.SerializationHelper.read(networkfile);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Train een neural netwerk met de data uit het bestand. Toon de resultaten
	 * van de training en sla de configuratie op
	 */
	public void train() {
		train(defaultTrainingFile);
	}

	/**
	 * Train met data
	 *
	 * @param trainingfile
	 *            Bestand met trainingsdata
	 */
	public void train(String trainingfile) {
		try {
			Instances trainData = readTrainingData(trainingfile);
			evaluateTrainingData(trainData);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * Sla geconfigureerd network op in standaardbestand
	 */
	public void saveNetwork() {
		saveNetwork(defaultNetworkFile);
	}

	/**
	 * Sla geconfigureerd network op in opgegeven bestand
	 *
	 * @param file
	 */
	public void saveNetwork(String file) {
		try {
			weka.core.SerializationHelper.write(file, mlp);
		} catch (Exception e) {
			logger.log(Level.WARNING, "Saving network failed : " + e.getMessage());
		}
	}

	/**
	 * Evalueer trainingsdata
	 *
	 * @param data
	 * @return
	 * @throws Exception
	 */
	private Evaluation evaluateTrainingData(Instances data) throws Exception {
		mlp.buildClassifier(data);
		Evaluation eval = new Evaluation(data);
		eval.evaluateModel(mlp, data);
		logger.log(Level.INFO, eval.toSummaryString(true));
		return eval;
	}

	/**
	 * Lees trainingsdata in
	 *
	 * @param trainingfile
	 * @return
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	private Instances readTrainingData(String trainingfile) throws FileNotFoundException, IOException {
		// Reading training arff file
		FileReader trainreader = new FileReader(trainingfile);
		Instances train = new Instances(trainreader);
		train.setClassIndex(train.numAttributes() - 1);
		logger.log(Level.INFO, "num attributes : " + train.numAttributes());
		logger.log(Level.INFO, "num classes    : " + train.numClasses());
		logger.log(Level.INFO, "num data items : " + train.numInstances());
		return train;
	}

	public String voorspel(String bestandsnaam) throws FileNotFoundException, IOException, Exception {
		// Lees instances
		BufferedReader reader = new BufferedReader(new FileReader(bestandsnaam));
		Instances datapredict = new Instances(reader);
		datapredict.setClassIndex(datapredict.numAttributes() - 1);
		Instances predicteddata = new Instances(datapredict);
		reader.close();
		// Predict instances
		for (int i = 0; i < datapredict.numInstances(); i++) {
			double clsLabel = mlp.classifyInstance(datapredict.instance(i));
			predicteddata.instance(i).setClassValue(clsLabel);
		}
		logger.log(Level.INFO,predicteddata.toString());
		// Save instances
		String outputBestand = bestandsnaam.substring(0,bestandsnaam.length() - 5) + "_solved.arff";
		BufferedWriter writer = new BufferedWriter(new FileWriter(outputBestand));
		writer.write(predicteddata.toString());
		writer.newLine();
		writer.flush();
		writer.close();

		return null;
	}
}
