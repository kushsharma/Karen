package main;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.StringTokenizer;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.CoreAnnotations.NamedEntityTagAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TextAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.sentiment.SentimentCoreAnnotations;
import edu.stanford.nlp.util.CoreMap;

/**
 * TODO:
 * make matcher/replacer better
 * fix database accordingly^
 * ask user name and store somewhere
 * use sentiment somewhere
 * 
 * **/
public class Processor {
	Properties props;
	StanfordCoreNLP pipeline;
	
	HashMap<String, String> user_data = new HashMap<String,String>();
	ArrayList<String> toks = new ArrayList<String>();
	File xmlFile = new File("src/main/resources/database.xml");

	public Processor() {
		//String text = "why sky blue";
		props = new Properties();
		props.setProperty("annotators",	"tokenize, ssplit, pos, lemma, parse, sentiment");
		pipeline = new StanfordCoreNLP(props);
	}

	public String getSentiment(String text) {

		Annotation annotation = pipeline.process(text);
		List<CoreMap> sentences = annotation.get(CoreAnnotations.SentencesAnnotation.class);
		
		String overall = "";
		for (CoreMap sentence : sentences) {
			String sentiment = sentence.get(SentimentCoreAnnotations.SentimentClass.class);
			overall = sentiment;
			System.out.println(sentiment + " : " + sentence);
		}
		
		return overall;
	}

	public String tokenize(String text) {
		Annotation annotation = pipeline.process(text);
		List<CoreMap> sentences = annotation.get(SentencesAnnotation.class);
		
		toks.clear();
		String t = "";
		for (CoreMap sentence : sentences)
		{
		    for (CoreLabel token : sentence.get(TokensAnnotation.class))
		    {
		        // Using the CoreLabel object we can start retrieving NLP annotation data
		        // Extracting the Text Entity
		        String tokenText = token.getString(TextAnnotation.class);

		        // Extracting Name Entity Recognition 
		        String ner = token.getString(NamedEntityTagAnnotation.class);

		        // Extracting Part Of Speech
		        String pos = token.get(CoreAnnotations.PartOfSpeechAnnotation.class);

		        // Extracting the Lemma
		        String lemma = token.get(CoreAnnotations.LemmaAnnotation.class);
		        lemma = checkSynonym(lemma); 

		        System.out.println("text=" + tokenText + ";NER=" + ner + ";POS=" + pos + ";LEMMA=" + lemma);

		        toks.add(lemma);		                
		    }
		}
		
		return t;
	}
	
	/**used to find synonyms and convert them**/
	private String checkSynonym(String lemma) {
		
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = null;
		Document doc  =null;
		try {
			dBuilder = dbFactory.newDocumentBuilder();
			doc = dBuilder.parse(xmlFile);
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} 
		
		doc.getDocumentElement().normalize();

		//System.out.println("Root element :" + doc.getDocumentElement().getNodeName());
				
		NodeList nList = doc.getElementsByTagName("word");
				
		//System.out.println("----------------------------");

		for (int temp = 0; temp < nList.getLength(); temp++) {

			Node nNode = nList.item(temp);
					
			//System.out.println("\nCurrent Element :" + nNode.getNodeName());
					
				Element eElement = (Element) nNode;
				String keyword = eElement.getAttribute("keyword");
				//System.out.println("Keyword : " + keyword);				
								
				NodeList nMatches = nNode.getChildNodes();//doc.getElementsByTagName("match");
				for(int i = 0; i< nMatches.getLength(); i++){
					Node mat = nMatches.item(i);
					String line = mat.getTextContent();	
					
					if(line.equals(lemma))
					{
						return keyword;
					}		
				}
		}
				
		return lemma;
	}

	/**convert sentence to look like a reply to user**/
	public String postConversion(String text){
		StringTokenizer st = new StringTokenizer(text);
		
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = null;
		Document doc  =null;
		try {
			dBuilder = dbFactory.newDocumentBuilder();
			doc = dBuilder.parse(xmlFile);
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} 
		
		doc.getDocumentElement().normalize();

		//System.out.println("Root element :" + doc.getDocumentElement().getNodeName());
				
		NodeList nList = doc.getElementsByTagName("postconvert");
				
		//System.out.println("----------------------------");
		
		while(st.hasMoreTokens()){
			String token = st.nextToken().toLowerCase();
			for (int temp = 0; temp < nList.getLength(); temp++) {
				Node nNode = nList.item(temp).getChildNodes().item(1);
				
				//System.out.println("\nCurrent Element "+token+":" + nNode.getNodeName() +","+ nNode.getTextContent());
				if(nNode.getNodeName().equals(token))
				{
					text = text.replaceAll("\\b"+token+"\\b",""+nNode.getTextContent()+"");
					MainClass.sop(text);
				}
			}			
		}
		
		return text;
	}
	
	public String processFromDB(String text){
		return processFromDB(text, null);
	}
	
	public String processFromDB(String text, String UseThisKeyword){
		
		String reply = "";
		
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = null;
		Document doc  =null;
		try {
			dBuilder = dbFactory.newDocumentBuilder();
			doc = dBuilder.parse(xmlFile);
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} 
		
		//read this - http://stackoverflow.com/questions/13786607/normalization-in-dom-parsing-with-java-how-does-it-work
		doc.getDocumentElement().normalize();

		System.out.println("Root element :" + doc.getDocumentElement().getNodeName());
				
		NodeList nList = doc.getElementsByTagName("block");
				
		System.out.println("----------------------------");

		for (int temp = 0; temp < nList.getLength(); temp++) {

			Node nNode = nList.item(temp);
					
			System.out.println("\nCurrent Element :" + nNode.getNodeName());
					
			if (nNode.getNodeType() == Node.ELEMENT_NODE) {

				Element eElement = (Element) nNode;
				String keyword = eElement.getAttribute("keyword");
				System.out.println("Keyword : " + keyword);
				
				//don't check lines if keyword does not match
				boolean checkIfRightNode = false;
				
				if(UseThisKeyword != null){
					//keyword force check
					if(keyword.equals(UseThisKeyword))
						checkIfRightNode = true;
				}
				else{
					//check for all keywords
					for(String s:toks){
						if(s.equals(keyword))
							checkIfRightNode = true;		
					}
				}
				
				System.out.println("Checking for match block..."+checkIfRightNode);
				NodeList nMatches = nNode.getChildNodes();//doc.getElementsByTagName("match");
				for(int i = 0; i< nMatches.getLength() && checkIfRightNode; i++){
					Node mat = nMatches.item(i);
					
					boolean SentenceMatch = true;
					String sentence = "";
					String replacer = ""; 
					if(mat.hasAttributes())
					{
						sentence = mat.getAttributes().getNamedItem("sentence").getTextContent();
						System.out.println("Sentence: "+sentence);

						String matchable = sentence.replaceAll("::", "").trim();
						System.out.println("Matches:"+matchable);
						StringTokenizer st = new StringTokenizer(matchable);
						
						boolean textMatch = false;
						
						String tempText = "";
						for(String s:toks)
							tempText += s+" ";
						MainClass.sop("text:"+tempText);
						if(tempText.matches("(.+)?"+matchable+"(.+)?"))
							textMatch = true;
						
						/*
						int sl  = st.countTokens();
						int iz = 0;
						
						while(st.hasMoreTokens()){
							String token = st.nextToken();
							for(String s:toks){
								//check if this is the current sentence match block
								if(s.equals(token)){
									iz++;
									if(iz == sl){
										textMatch = true;
										break;
									}									
								}
							}
							
						}
						*/
						if(textMatch){
							//sentence found is user text
							//get things that are important
							
							
							
							String postfix = tempText.substring(tempText.indexOf(matchable) + matchable.length(), tempText.length()).trim();
							//replacer = postfix.split(" ")[0]; //first word
							
							//replacer = replacer.replaceAll("[^a-zA-Z0-9]", "");
							replacer = postfix.replaceAll("[^a-zA-Z0-9 ]", "");
							
							replacer = postConversion(replacer);
							System.out.println("word to use:"+replacer);
							
							//dont loop for more sentences
							//checkIfRightNode = false;
						}
						else
							SentenceMatch = false;
					}
					
					
					if(mat.getNodeType() == Node.ELEMENT_NODE && SentenceMatch){
						
						Element eMat = (Element) mat;
						NodeList nLines = eMat.getElementsByTagName("line");
						
						for(int j=0;j<nLines.getLength();j++){
							
							//choose randomly
							j = (int) (Math.random() * nLines.getLength());

							Node l = nLines.item(j);
							String line = l.getTextContent();						
							if(line.split(" ")[0].trim().equals("goto")){
								//if jump statement is found
								reply = processFromDB(text, line.split(" ")[1].trim());
								
								//don't go further
								return reply;
							}
							
							//modify current line accourding to user
							reply = line.replace(":2:", replacer);
							
							System.out.println("Line : " + line);
							
							//don't go further
							return reply;
						}
					}

					
//					NodeList nLines = mat.getChildNodes();//doc.getElementsByTagName("line");
//					for(int j=0;j<nLines.getLength();j++){
//						Node line = nLines.item(j);
//						
//						if (line.getNodeType() == Node.ELEMENT_NODE) {
//
//							Element elm = (Element) line;
//							
//						}
//					}

				}//lines
				
				//System.out.println("First Name : " + eElement.getElementsByTagName("firstname").item(0).getTextContent());
				//System.out.println("Last Name : " + eElement.getElementsByTagName("lastname").item(0).getTextContent());
				//System.out.println("Nick Name : " + eElement.getElementsByTagName("nickname").item(0).getTextContent());
				//System.out.println("Salary : " + eElement.getElementsByTagName("salary").item(0).getTextContent());

			}//end matches
		}//end blocks
	    
		//if no replay found, get a default one
		if(reply.equals("")){
			reply = processFromDB(text, "xnonex");
		}
	  
		return reply;
	}
	
	public void putData(String key, String data){
		user_data.put(key, data);		
	}
	
	public String getData(String key){
		return user_data.get(key);		
	}
}
