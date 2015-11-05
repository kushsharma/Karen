package main;

import java.io.DataInputStream;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import edu.stanford.nlp.dcoref.CorefChain;
import edu.stanford.nlp.dcoref.CorefCoreAnnotations.CorefChainAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreAnnotations.NamedEntityTagAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.PartOfSpeechAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TextAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.IndexedWord;
import edu.stanford.nlp.neural.rnn.RNNCoreAnnotations;
import edu.stanford.nlp.parser.lexparser.LexicalizedParser;
import edu.stanford.nlp.pipeline.*;
import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.semgraph.SemanticGraphCoreAnnotations.CollapsedCCProcessedDependenciesAnnotation;
import edu.stanford.nlp.semgraph.SemanticGraphCoreAnnotations.CollapsedDependenciesAnnotation;
import edu.stanford.nlp.semgraph.SemanticGraphEdge;
import edu.stanford.nlp.sentiment.SentimentCoreAnnotations;
import edu.stanford.nlp.sentiment.SentimentCoreAnnotations.SentimentAnnotatedTree;
import edu.stanford.nlp.trees.GrammaticalRelation;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TreeCoreAnnotations;
import edu.stanford.nlp.trees.TreeCoreAnnotations.TreeAnnotation;
import edu.stanford.nlp.util.*;

public class MainClass {
	
	public static final boolean DEBUG = true;
	
	public static void sop(float t){
		if(DEBUG)
			System.out.println(t);
	}
	
	public static void sop(String t){
		if(DEBUG)
			System.out.println(t);
	}
	
	public static void main(String args[]){
		//new MainClass().sentiment();
		Processor process = new Processor();
		UserInterface ui = new UserInterface(process);
		
		if(true)
		return;
		
		/* First step is initiating the Stanford CoreNLP pipeline 
		   (the pipeline will be later used to evaluate the text and annotate it)
		   Pipeline is initiated using a Properties object which is used for setting all needed entities, 
		   annotations, training data and so on, in order to customized the pipeline initialization to 
		   contains only the models you need */
		Properties props = new Properties();

		/* The "annotators" property key tells the pipeline which entities should be initiated with our
		     pipeline object, See http://nlp.stanford.edu/software/corenlp.shtml for a complete reference 
		     to the "annotators" values you can set here and what they will contribute to the analyzing process  */
		props.put( "annotators", "tokenize, ssplit, pos, lemma, ner, regexner, parse, dcoref, sentiment" );
		StanfordCoreNLP pipeLine = new StanfordCoreNLP( props );

		/* Next we can add customized annotation and trained data 
		   I will elaborate on training data in my next blog chapter, for now you can comment those lines */
		//pipeLine.addAnnotator(new RegexNERAnnotator("some RegexNer structured file"));
		//pipeLine.addAnnotator(new TokensRegexAnnotator("some tokenRegex structured file"));

		// Next we generate an annotation object that we will use to annotate the text with
		SimpleDateFormat formatter = new SimpleDateFormat( "yyyy-MM-dd" );
		String currentTime = formatter.format( System.currentTimeMillis() );

		// inputText will be the text to evaluate in this example
		String inputText = "some text to evaluate";
		Annotation document = new Annotation( inputText );
		document.set( CoreAnnotations.DocDateAnnotation.class, currentTime );

		// Finally we use the pipeline to annotate the document we created
		pipeLine.annotate( document );

		/* now that we have the document (wrapping our inputText) annotated we can extract the
		    annotated sentences from it, Annotated sentences are represent by a CoreMap Object */
		List<CoreMap> sentences = document.get(SentencesAnnotation.class);

		/* Next we can go over the annotated sentences and extract the annotated words,
		    Using the CoreLabel Object */
		for (CoreMap sentence : sentences)
		{
		    for (CoreLabel token : sentence.get(TokensAnnotation.class))
		    {
		        // Using the CoreLabel object we can start retrieving NLP annotation data
		        // Extracting the Text Entity
		        String text = token.getString(TextAnnotation.class);

		        // Extracting Name Entity Recognition 
		        String ner = token.getString(NamedEntityTagAnnotation.class);

		        // Extracting Part Of Speech
		        String pos = token.get(CoreAnnotations.PartOfSpeechAnnotation.class);

		        // Extracting the Lemma
		        String lemma = token.get(CoreAnnotations.LemmaAnnotation.class);
		        System.out.println("text=" + text + ";NER=" + ner +
		                        ";POS=" + pos + ";LEMMA=" + lemma);

		        /* There are more annotation that are available for extracting 
		            (depending on which "annotators" you initiated with the pipeline properties", 
		            examine the token, sentence and document objects to find any relevant annotation 
		            you might need */
		    }

		    /* Next we will extract the SemanitcGraph to examine the connection 
		       between the words in our evaluated sentence */
		    SemanticGraph dependencies = sentence.get
		                                (CollapsedDependenciesAnnotation.class);

		    /* The IndexedWord object is very similar to the CoreLabel object 
		        only is used in the SemanticGraph context */
		    IndexedWord firstRoot = dependencies.getFirstRoot();
		    List<SemanticGraphEdge> incomingEdgesSorted =
		                                dependencies.getIncomingEdgesSorted(firstRoot);

		    for(SemanticGraphEdge edge : incomingEdgesSorted)
		    {
		        // Getting the target node with attached edges
		        IndexedWord dep = edge.getDependent();

		        // Getting the source node with attached edges
		        IndexedWord gov = edge.getGovernor();

		        // Get the relation name between them
		        GrammaticalRelation relation = edge.getRelation();
		    }
		  
		    // this section is same as above just we retrieve the OutEdges
		    List<SemanticGraphEdge> outEdgesSorted = dependencies.getOutEdgesSorted(firstRoot);
		    for(SemanticGraphEdge edge : outEdgesSorted)
		    {
		        IndexedWord dep = edge.getDependent();
		        System.out.println("Dependent=" + dep);
		        IndexedWord gov = edge.getGovernor();
		        System.out.println("Governor=" + gov);
		        GrammaticalRelation relation = edge.getRelation();
		        System.out.println("Relation=" + relation);
		   }
		}
	}
	
	public void another(){
		
		Properties props = new Properties();
	    props.put("annotators", "tokenize, ssplit, pos, lemma, ner, parse, dcoref");
	    StanfordCoreNLP pipeline = new StanfordCoreNLP(props);

	    // read some text in the text variable
	    String text = "the quick browm fox jumps over the lazy dog";

	    // create an empty Annotation just with the given text
	    Annotation document = new Annotation(text);

	    // run all Annotators on this text
	    pipeline.annotate(document);

	    // these are all the sentences in this document
	    // a CoreMap is essentially a Map that uses class objects as keys and has values with custom types
	    List<CoreMap> sentences = document.get(SentencesAnnotation.class);

	    for(CoreMap sentence: sentences) {
	      // traversing the words in the current sentence
	      // a CoreLabel is a CoreMap with additional token-specific methods
	      for (CoreLabel token: sentence.get(TokensAnnotation.class)) {
	        // this is the text of the token
	        String word = token.get(TextAnnotation.class);
	        // this is the POS tag of the token
	        String pos = token.get(PartOfSpeechAnnotation.class);
	        // this is the NER label of the token
	        String ne = token.get(NamedEntityTagAnnotation.class);       
	      }

	      // this is the parse tree of the current sentence
	      Tree tree = sentence.get(TreeAnnotation.class);

	      // this is the Stanford dependency graph of the current sentence
	      SemanticGraph dependencies = sentence.get(CollapsedCCProcessedDependenciesAnnotation.class);
	    }

	    // This is the coreference link graph
	    // Each chain stores a set of mentions that link to each other,
	    // along with a method for getting the most representative mention
	    // Both sentence and token offsets start at 1!
	    Map<Integer, CorefChain> graph = 
	      document.get(CorefChainAnnotation.class);
		
	}
	
	public void anothertwo(){
		PrintWriter out;
	    out = new PrintWriter(System.out);
	   
	    StanfordCoreNLP pipeline = new StanfordCoreNLP();
	    Annotation annotation;
	    annotation = new Annotation("Kosgi Santosh sent an email to Stanford University. He didn't get a reply.");
	    

	    pipeline.annotate(annotation);
	    pipeline.prettyPrint(annotation, out);
	    
	    // An Annotation is a Map and you can get and use the various analyses individually.
	    // For instance, this gets the parse tree of the first sentence in the text.
	    List<CoreMap> sentences = annotation.get(CoreAnnotations.SentencesAnnotation.class);
	    if (sentences != null && sentences.size() > 0) {
	      CoreMap sentence = sentences.get(0);
	      Tree tree = sentence.get(TreeCoreAnnotations.TreeAnnotation.class);
	      out.println();
	      out.println("The first sentence parsed is:");
	      tree.pennPrint(out);
	    }
		
	}
	
	public void sentiment(){
		
		String text = "why sky blue";
		Properties props = new Properties();
		props.setProperty("annotators", "tokenize, ssplit, pos, lemma, parse, sentiment");
		StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
		
		Annotation annotation = pipeline.process(text);
		List<CoreMap> sentences = annotation.get(CoreAnnotations.SentencesAnnotation.class);
		for (CoreMap sentence : sentences) {
		  String sentiment = sentence.get(SentimentCoreAnnotations.SentimentClass.class);
			//Tree tree = sentence.get(SentimentAnnotatedTree.class);
            //int sentiment = RNNCoreAnnotations.getPredictedClass(tree);
            //String partText = sentence.toString();
                
		  System.out.println(sentiment + " : " + sentence);
		}
		
	}
	
	public void LexParse(){
		
//		LexicalizedParser lp1 = new LexicalizedParser("englishPCFG.ser.gz", new Options());
//		String sentence = "It is a fine day today";
//		Tree parse = lp.parse(sentence);
	}
}
