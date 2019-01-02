package moa.experiment;

import java.io.File;


public class Experiment10 { 
	
	public static String inputFolder = "E:\\GITRepo\\thesis\\data\\arff\\" ;	 
	public static String OutputFolder = "E:\\GITRepo\\thesis\\data\\del\\del\\" ;
	public final static int FIXED_NONE = -1000 ;
	
	public final static boolean train = true;
//	'C'
	public final static String [] regression = {
			" tskstreams.learner.FuzzyLearner -b 40  -c 0.01 -t 0.05 -p -d 0.001 -l 0.1 -R -C 0", 
			" tskstreams.learner.FuzzyLearner -b 40  -c 0.01 -t 0.05 -d 0.001 -l 0.1 -R -C 0",
			" tskstreams.learner.FuzzyLearner -b 40  -c 0.01 -t 0.05 -p -d 0.001 -l 0.1 -C 0", 
			" tskstreams.learner.FuzzyLearner -b 40  -c 0.01 -t 0.05 -d 0.001 -l 0.1 -C 0",

			" tskstreams.learner.FuzzyLearner -b 40  -c 0.01 -t 0.05 -p -d 0.001 -l 0.1 -R -C 1", 
			" tskstreams.learner.FuzzyLearner -b 40  -c 0.01 -t 0.05 -d 0.001 -l 0.1 -R -C 1",
			" tskstreams.learner.FuzzyLearner -b 40  -c 0.01 -t 0.05 -p -d 0.001 -l 0.1 -C 1", 
			" tskstreams.learner.FuzzyLearner -b 40  -c 0.01 -t 0.05 -d 0.001 -l 0.1 -C 1",
			
			} ;

	public final static String [] regressionName = {
			"FuzzyLearnerPenalizeRC0",
			"FuzzyLearnerRC0",
			"FuzzyLearnerPenalizeC0",
			"FuzzyLearnerC0",

			"FuzzyLearnerPenalizeRC1",
			"FuzzyLearnerRC1",
			"FuzzyLearnerPenalizeC1",
			"FuzzyLearnerC1",
			
//			"FuzzyLearnerC1",
//			"FuzzyLearnerC0",
	} ;
	
	public final static String [] driftDetection = {
//			" -m 0 " ," -m 1 "," -m 2 "
			" -m 0 "
	} ;
	
	public final static String [] driftName = {
//			"M0","M1","M2"
			"M0"
	} ;	
	
	public final static String [] normalizeMethod = {
//			" -n 0 "," -n 1 "," -n 2 "
			" -n 0 "
	} ;
	
	public final static String [] normalizeName = {
//			"N0","N1","N2"
			"N0"
	} ;

	public final static String [] regressionOthers = {			 
			" moa.classifiers.rules.AMRulesRegressor -c 0.01 -t 0.05 -L (rules.multilabel.functions.AdaptiveMultiTargetRegressor -l moa.classifiers.rules.multilabel.functions.MultiTargetMeanRegressor -m moa.classifiers.rules.multilabel.functions.MultiTargetPerceptronRegressor) -A (OddsRatioScore -p CantellisInequality) -O SelectAllOutputs", 
			" moa.classifiers.trees.FIMTDD -s VarianceReductionSplitCriterion -c 0.01 -t 0.05 -l 0.01 "
			} ;

	public final static String [] regressionNameOthers = {
			"AMRulesRegressor",
			"FIMTDD"
	} ;
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
//		String temp = "EvaluateInterleavedTestThenTrain -l ( tskstreams.learner.FuzzyLearner "
//				+ "-b 40  -c 0.05 -t 0.05 -d 0.001 -l 0.01  -m 2  -n 0 )  "
//				+ "-s (ConceptDriftStream -s ( moa.generator.HyperplaneGeneratorReg -i 113 -a 10 -m 1 ) "
//				+ "-d ( moa.generator.HyperplaneGeneratorReg -i 673 -a 10 -m 1 ) -p 25000 -w 1)  "
//				+ "-e (WindowRegressionPerformanceEvaluator -w 250 ) -f 250 -i 50000 -d "
//				+ "D:\\gitProjects\\waleri-fuzzy-rules\\data\\del\\FuzzyLearner-M2-N0-CDW1-Dis[113]ZZ-ZZDis[673].csv" ;
//		
//		System.out.println(temp);
//		moa.DoTask.main(temp.split("    ")) ;
//		System.exit(0);
		
		File folder = new File(inputFolder) ;
		
//		for (int i =0 ; i < regressionOthers.length ; i++){
//			testDrift(regressionOthers[i], regressionNameOthers[i]);
//			testPure(regressionOthers[i], regressionNameOthers[i]);	
////			System.out.println(regression[i]+driftDetection[j]+ normalizeMethod[k]);
////			System.out.println(regressionName[i]+"-"+ driftName[j] +"-"+normalizeName[k]);
//			System.out.println("####################");
//
//		}
//		
//		System.exit(0);
		
//		for (int i =0 ; i < regression.length ; i++){
//			for (int j =0 ; j < driftDetection.length ; j++){
//				for (int k =0 ; k < normalizeMethod.length ; k++){
//					testDrift(regression[i]+driftDetection[j]+ normalizeMethod[k], regressionName[i]+"-"+ driftName[j] +"-"+normalizeName[k]);
//					testPure(regression[i]+driftDetection[j]+ normalizeMethod[k], regressionName[i]+"-"+ driftName[j] +"-"+normalizeName[k]);	
//					System.out.println(regression[i]+driftDetection[j]+ normalizeMethod[k]);
//					System.out.println(regressionName[i]+"-"+ driftName[j] +"-"+normalizeName[k]);
//					System.out.println("####################");
//
//				}
//			}
//		}
//		
//		for (int i =0 ; i < regressionOthers.length ; i++){
//				testDrift(regressionOthers[i], regressionNameOthers[i]);
//				testPure(regressionOthers[i], regressionNameOthers[i]);	
//				System.out.println(regressionOthers[i]);
//				System.out.println(regressionNameOthers[i]);
//				System.out.println("####################");
//		}
//		
		for (int rnd = 113; rnd < 123; rnd++) {
		
			for (File file:folder.listFiles()){
				String fileStr = file.getAbsolutePath() ;
				if (!fileStr.endsWith(".arff"))
					continue ;
				System.out.println(fileStr);
				String shuffeledStreams = " (CacheShuffledStream -s (ArffFileStream -f " + fileStr + ") -r "
						+ rnd + " ) ";

				for (int i =0 ; i < regression.length ; i++){
					for (int j =0 ; j < driftDetection.length ; j++){
						for (int k =0 ; k < normalizeMethod.length ; k++){
	
							String outputFile = regressionName[i]+"-"+ driftName[j] +"-"+normalizeName[k]  +"-" + rnd + "-" + file.getName() ;
							String command  = "EvaluateInterleavedTestThenTrain -l ("+regression[i]+driftDetection[j]+ normalizeMethod[k]+") -s "
									+ shuffeledStreams + " -e (WindowRegressionPerformanceEvaluator -w 250 ) -f 250 -d "
											+ OutputFolder+outputFile+".csv" ;
							
//							String command  = "EvaluateInterleavedTestThenTrain -l ("+regression[i]+driftDetection[j]+ normalizeMethod[k]+") -s "
//									+ "( ArffFileStream -f "+ fileStr+" ) -e (WindowRegressionPerformanceEvaluator -w 250 ) -f 250 -d "
//											+ OutputFolder+outputFile+".csv" ;				
							System.out.println(command);
							moa.DoTask.main(command.split("    ")) ;
							System.out.println(command);
							
						}
					}				
				}
				
				for (int i = 0; i < regressionOthers.length; i++) {
					String outputFile = regressionNameOthers[i] + "-" + rnd + "-" + file.getName() ;
					String command  = "EvaluateInterleavedTestThenTrain -l ("+regressionOthers[i]+") -s "
							+ shuffeledStreams + " -e (WindowRegressionPerformanceEvaluator -w 250 ) -f 250 -d "
									+ OutputFolder+outputFile+".csv" ;	
//					String command  = "EvaluateInterleavedTestThenTrain -l ("+regressionOthers[i]+") -s "
//							+ "( ArffFileStream -f "+ fileStr+" ) -e (WindowRegressionPerformanceEvaluator -w 250 ) -f 250 -d "
//									+ OutputFolder+outputFile+".csv" ;
					System.out.println(command);
					moa.DoTask.main(command.split("    "));
					System.out.println(command);
				}
			}
		}
	}
	
	public static void testDrift(String lernenr,String lernerAbb) {
	
//		int seed = 0 ; int s1 = 0 ; int s2 =0 ; int d = 0 ;
		for (int seed = 0; seed < seedsStream1.length; seed++) {
			for (int s1 = 0; s1 < streams.length; s1++) {
				for (int s2 = 0; s2 < streams.length; s2++) {
					for (int d = 0; d < driftStreams.length; d++) {
						String stream1 = streams[s1].replaceFirst("xxx", Integer.toString(seedsStream1[seed]));
						String stream2 = streams[s2].replace("xxx", Integer.toString(seedsStream2[seed]));
						String drift = driftStreams[d].replace("stream1", stream1).replace("stream2", stream2);
						String outputFile = lernerAbb +"-"+ driftStreamsAbb[d] +"-"+ streamsAbb[s1] + "["+ seedsStream1[seed] + "]"
						+"ZZ-ZZ"+ streamsAbb[s2] + "["+ seedsStream2[seed] + "]" ;
	
						String command = "EvaluateInterleavedTestThenTrain -l (" + lernenr + ") " + drift
								+ " -e (WindowRegressionPerformanceEvaluator -w 250 ) -f 250 -i 50000 -d "
								+ OutputFolder + outputFile + ".csv";
						
						System.out.println(command);
						moa.DoTask.main(command.split("    ")) ;
						System.out.println(command);
	
					}
				}
			}
		}
	}
	
	public static void testPure(String lernenr, String lernerAbb) {
		for (int seed = 0; seed < seedsStream1.length; seed++) {
			for (int s1 = 0; s1 < streams.length; s1++) {
	
				String stream1 = streams[s1].replaceFirst("xxx", Integer.toString(seedsStream1[seed]));
				String outputFile = lernerAbb + "-" + streamsAbb[s1] + "[" + seedsStream1[seed] + "]";
	
				String command = "EvaluateInterleavedTestThenTrain -l (" + lernenr + ") " + " -s (" + stream1 + ") "
						+ " -e (WindowRegressionPerformanceEvaluator -w 250 ) -f 250 -i 50000 -d " + OutputFolder
						+ outputFile + ".csv";
				System.out.println(command);
				moa.DoTask.main(command.split("    ")) ;
			}
		}
	
		for (int seed = 0; seed < seedsStream2.length; seed++) {
			for (int s2 = 0; s2 < streams.length; s2++) {
	
				String stream2 = streams[s2].replaceFirst("xxx", Integer.toString(seedsStream2[seed]));
				String outputFile = lernerAbb + "-" + streamsAbb[s2] + "[" + seedsStream2[seed] + "]";
	
				String command = "EvaluateInterleavedTestThenTrain -l (" + lernenr + ") " + " -s (" + stream2  + ") "
						+ " -e (WindowRegressionPerformanceEvaluator -w 250 ) -f 250 -i 50000 -d " + OutputFolder
						+ outputFile + ".csv";
				System.out.println(command);
				moa.DoTask.main(command.split("    ")) ;
				System.out.println(command);
			}
		}
	}


//	public static String[] streams = { " moa.generator.HyperplaneGeneratorReg -i xxx -a 2 -m 1 "};
	
	public static String[] streams = { " moa.generator.HyperplaneGeneratorReg -i xxx -a 10 -m 1 ",
			" moa.generator.HyperplaneGeneratorReg -i xxx -a 10 -m 2 ",
			" moa.generator.HyperplaneGeneratorReg -i xxx -a 10 -m 3 " };

	public static String[] streamsAbb = { "Dis", "SeqDis", "CubDis" };

	public static String[] driftStreams = { " -s (ConceptDriftStream -s (stream1) -d (stream2) -p 25000 -w 1) ",
			" -s (ConceptDriftStream -s (stream1) -d (stream2) -p 25000 -w 5000) ",
			" -s (ConceptDriftStream -s (stream1) -d (stream2) -p 25000 -w 10000) " };

	public static String[] driftStreamsAbb = { "CDW1", "CDW5k", "CDW10k" };

	public static int[] seedsStream1 = { 113 };
	public static int[] seedsStream2 = { 673 };

}




//public static void testPure(String lernenr, String lernerAbb) {
//	for (int seed = 0; seed < seedsStream1.length; seed++) {
//		for (int s1 = 0; s1 < streams.length; s1++) {
//
//			String stream1 = streams[s1].replaceFirst("xxx", Integer.toString(seedsStream1[seed]));
//			String outputFile = lernerAbb + "-" + streamsAbb[s1] + "[" + seedsStream1[seed] + "]";
//
//			String command = "EvaluateInterleavedTestThenTrain -l (" + lernenr + ") " + " -s (" + stream1 + ") "
//					+ " -e (WindowRegressionPerformanceEvaluator -w 250 ) -f 250 -i 50000 -d " + OutputFolder
//					+ outputFile + ".csv";
//			System.out.println(command);
//			moa.DoTask.main(command.split("    ")) ;
//		}
//	}
//
////	for (int seed = 0; seed < seedsStream2.length; seed++) {
////		for (int s2 = 0; s2 < streams.length; s2++) {
////
////			String stream2 = streams[s2].replaceFirst("xxx", Integer.toString(seedsStream2[seed]));
////			String outputFile = lernerAbb + "-" + streamsAbb[s2] + "[" + seedsStream2[seed] + "]";
////
////			String command = "EvaluateInterleavedTestThenTrain -l (" + lernenr + ") " + " -s (" + stream2  + ") "
////					+ " -e (WindowRegressionPerformanceEvaluator -w 250 ) -f 250 -i 50000 -d " + OutputFolder
////					+ outputFile + ".csv";
////			System.out.println(command);
////			moa.DoTask.main(command.split("    ")) ;
////			System.out.println(command);
////		}
////	}
//}
//
//public static void testDrift(String lernenr,String lernerAbb) {
//
//	int seed = 0 ; int s1 = 0 ; int s2 =0 ; int d = 0 ;
////	for (int seed = 0; seed < seedsStream1.length; seed++) {
////		for (int s1 = 0; s1 < streams.length; s1++) {
////			for (int s2 = 0; s2 < streams.length; s2++) {
////				for (int d = 0; d < driftStreams.length; d++) {
//					String stream1 = streams[s1].replaceFirst("xxx", Integer.toString(seedsStream1[seed]));
//					String stream2 = streams[s2].replace("xxx", Integer.toString(seedsStream2[seed]));
//					String drift = driftStreams[d].replace("stream1", stream1).replace("stream2", stream2);
//					String outputFile = lernerAbb +"-"+ driftStreamsAbb[d] +"-"+ streamsAbb[s1] + "["+ seedsStream1[seed] + "]"
//					+"ZZ-ZZ"+ streamsAbb[s2] + "["+ seedsStream2[seed] + "]" ;
//
//					String command = "EvaluateInterleavedTestThenTrain -l (" + lernenr + ") " + drift
//							+ " -e (WindowRegressionPerformanceEvaluator -w 250 ) -f 250 -i 50000 -d "
//							+ OutputFolder + outputFile + ".csv";
//					
//					System.out.println(command);
//					moa.DoTask.main(command.split("    ")) ;
//					System.out.println(command);
//
////				}
////			}
////		}
////	}
//}
