package com.techgig.hubot.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;
import com.google.gson.JsonParser;
import com.offbytwo.jenkins.JenkinsServer;
import com.offbytwo.jenkins.model.Build;
import com.offbytwo.jenkins.model.Job;
import com.offbytwo.jenkins.model.JobWithDetails;
import com.offbytwo.jenkins.model.QueueReference;
import com.techgig.hubot.model.ChatInformation;

import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import ai.api.AIConfiguration;
import ai.api.AIDataService;
import ai.api.AIServiceContext;
import ai.api.AIServiceContextBuilder;
import ai.api.model.AIRequest;
import ai.api.model.AIResponse;

@RestController
public class HubotController {
	HttpURLConnection connection = null;
	  @RequestMapping(value = "/Slackbot", headers="Content-Type=application/x-www-form-urlencoded", method = RequestMethod.POST)
	    public String slackbot(HttpServletRequest request) {
	    	   try {
	    		   
	    		   // api.ai check
	    		   
	    		 //  callApiAi("");
	    		   
	    		   
	    	         // Create connection
	    	         final URL url = new URL("https://hooks.slack.com/services/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");
	    	         connection = (HttpURLConnection) url.openConnection();
	    	         connection.setRequestMethod("POST");
	    	         connection.setConnectTimeout(5000);
	    	         connection.setUseCaches(false);
	    	         connection.setDoInput(true);
	    	         connection.setDoOutput(true);

	    	         final String payload = "payload="
	    	                 + URLEncoder.encode("{\"channel\":\"#devops\",\"username\":\"Sizzler-DevOps\",\"text\":"+request.getParameter("command")+",\"icon_emoji\":\":happy:\"}", "UTF-8");

	    	         // Send request
	    	         final DataOutputStream wr = new DataOutputStream(
	    	                 connection.getOutputStream());
	    	         wr.writeBytes(payload);
	    	         wr.flush();
	    	         wr.close();
	    	        // System.out.println(sampleProperty.getStringProp1());
	    	         // Get Response
	    	         final InputStream is = connection.getInputStream();
	    	         final BufferedReader rd = new BufferedReader(new InputStreamReader(is));
	    	         String line;
	    	         StringBuilder response = new StringBuilder();
	    	         while ((line = rd.readLine()) != null) {
	    	             response.append(line);
	    	             response.append('\n');
	    	         }

	    	         rd.close();
	    	         return response.toString();
	    	     } catch (Exception e) {
	    	        e.printStackTrace();
	    	        return "error--->"+e.getMessage();
	    	     } finally {
	    	         if (connection != null) {
	    	             connection.disconnect();
	    	         }
	    	     }
	      //  return "Successfully Posted in Slack!";
	   
	    
	}
	  
	  
	  
	    public String triggerJenkins1() throws ParseException, IOException{
		  String responsestr,responseOutput="",gson,reqGson="";
	    	   try {
	    		   
	    		   AIConfiguration configuration = new AIConfiguration("1df2a5ba115a4e7d81f2c91396db2a00");

	    		    AIDataService dataService = new AIDataService(configuration);
ChatInformation ci = new ChatInformation();
	    		    String resp,email="";
	    		    
	    		  boolean accessFlag=false;

	    		          try {
	    		        	  
	    		        	  JenkinsServer jenkins=null;
	    		        	  
	    		        	  File jsonDir= new File(".");
	    		        	  System.out.println(jsonDir.getCanonicalPath());
//	    		        	  
//	    		        	  
	    		        	  JsonParser parser = new JsonParser();
	    		        	  Object obj= parser.parse(jsonDir.getCanonicalPath()+"\\src\\main\\resources\\config.json");
	    		        	  
	    		        	  
	    		        	  JSONObject jsonObject=(JSONObject) obj;
//	    		        	  
	    		        	  String jenkinsUrl = (String) jsonObject.get("jenkinsUrl");
	    		        	  
	    		        	  JSONObject jsonObject1=(JSONObject)jsonObject.get("job");
//	    		        	  

	    		        	  String acl = (String) jsonObject.get("acl");
//	    		        	  
	    		        	  if(acl !=null) {
//	    		        		  
	    		        		  String[] aclArray = acl.split(",");
	    		        		  
	    		        		  if(aclArray.length > 0) {
	    		        			  
	    		        			  for (int i=0;i<aclArray.length;i++) {
	    		        				  
	    		        				  if(email.toLowerCase().equals(aclArray[i].toLowerCase())) {
	    		        					  accessFlag =true;
	    		        					  break;
	    		        				  }
	    		        			  }
	    		        		  }
	    		        		  
	    		        	  }
	    		        	  
	    		        	  
	    		        	  
	    		        	  String env="";
							//String jobName= jsonObject1.get(env).toString().trim();
							
							//if(accessFlag) {
	    		        	  
	    		        	  //http://192.168.1.7:8080/jenkins/buildByToken/build?job=jenkis.test&token=venkat
								
								try {
									jenkins= new JenkinsServer(new URI("http://192.168.1.7:8080/jenkins/"),"admin","admin");
								}
								catch(Exception e) {
									e.printStackTrace();
								}
								
								Map<String,Job> jobs=jenkins.getJobs();
								
								
								
								JobWithDetails job = jobs							
										.get("jenkis.test").details();
								
								HashMap <String,String> map = new HashMap<String,String>();
								
								map.put("token", "venkat");
								System.out.println(job.getName());
								
								List<Build> list = job.getBuilds();
								System.out.println(job.getNextBuildNumber());
								for(int i=0;i<list.size();i++) {
									System.out.println(list.get(i).getNumber());
								}
								JobWithDetails job1 = jenkins.getJob("jenkis.test");
								
								String url = "http://192.168.1.7:8080/jenkins/buildByToken/build?job=jenkis.test&token=venkat";

								URL urlObj = new URL(url);
								HttpURLConnection con = (HttpURLConnection) urlObj.openConnection();

								// optional default is GET
								con.setRequestMethod("GET");

								//add request header
								con.setRequestProperty("User-Agent", "\"Mozilla/5.0\";");

								int responseCode = con.getResponseCode();
								System.out.println("responseCode--->"+responseCode);
								
						        //QueueReference queueRef = job1.build(map, true);

								//job.build(map);
								
								//map.put("mail_id", "sizzlers.hack@gmail.com");
								//job.build();
								//job.build(map);
								
								
								
								ci.setDesc("Deployment Started");
								ci.setSpeech("Speech from api");
								ci.setDisplayText(""+job.getNextBuildNumber());
								ci.setSource(job.getUrl());
								
								Gson	gson1 = new Gson();
								
								responseOutput=gson1.toJson(ci);
								
								
								
								
						//	}
	    		        	  
	    		          } catch (Exception ex) {
	    		            ex.printStackTrace();
	    		          }

	    		        
	    	   } catch (Exception e) {
	    	        e.printStackTrace();
	    	        return "error--->"+e.getMessage();
	    	     } finally {
	    	         if (connection != null) {
	    	             connection.disconnect();
	    	         }
	    	     }
	        return responseOutput;
	   
	    
	}
	  
	  
	  
	  @RequestMapping("/apiai")
	    public String triggerJenkins() throws ParseException, IOException{
		  String responsestr,responseOutput="",gson,reqGson="";
	    	   try {
	    		   
	    		   AIConfiguration configuration = new AIConfiguration("1df2a5ba115a4e7d81f2c91396db2a00");

	    		    AIDataService dataService = new AIDataService(configuration);
ChatInformation ci = new ChatInformation();
	    		    String resp,email="";
	    		    
	    		  boolean accessFlag=false;

	    		          try {
	    		        	  
	    		        	  JenkinsServer jenkins=null;
	    		        	  
//	    		        	  File jsonDir= new File(".");
//	    		        	  System.out.println(jsonDir.getCanonicalPath());
//	    		        	  
//	    		        	  
//	    		        	  JsonParser parser = new JsonParser();
//	    		        	  Object obj= parser.parse(jsonDir.getCanonicalPath()+"\\src\\main\\resources\\config.json");
	    		        	  
	    		        	  
//	    		        	  JSONObject jsonObject=(JSONObject) obj;
//	    		        	  
//	    		        	  String jenkinsUrl = (String) jsonObject.get("jenkinsUrl");
//	    		        	  
//	    		        	  JSONObject jsonObject1=(JSONObject)jsonObject.get("job");
//	    		        	  
//	    		        	  if(fromUser!=null) {
//	    		        		  int emailIndex = fromUser.indexOf("/");
//	    		        		  
//	    		        		  email = fromUser.substring(0, emailIndex);
//	    		        		  
//	    		        		  
//	    		        	  }
//	    		        	  
//	    		        	  String acl = (String) jsonObject.get("acl");
//	    		        	  
//	    		        	  if(acl !=null) {
//	    		        		  
//	    		        		  String[] aclArray = acl.split(",");
//	    		        		  
//	    		        		  if(aclArray.length > 0) {
//	    		        			  
//	    		        			  for (int i=0;i<aclArray.length;i++) {
//	    		        				  
//	    		        				  if(email.toLowerCase().equals(aclArray[i].toLowerCase())) {
//	    		        					  accessFlag =true;
//	    		        					  break;
//	    		        				  }
//	    		        			  }
//	    		        		  }
//	    		        		  
//	    		        	  }
	    		        	  
	    		        	  
	    		        	  
	    		        	  String env="";
							//String jobName= jsonObject1.get(env).toString().trim();
							
							//if(accessFlag) {
	    		        	  
	    		        	  //http://192.168.1.7:8080/jenkins/buildByToken/build?job=jenkis.test&token=venkat
								
								try {
									jenkins= new JenkinsServer(new URI("http://192.168.1.7:8080/jenkins/"),"admin","admin");
								}
								catch(Exception e) {
									e.printStackTrace();
								}
								
								Map<String,Job> jobs=jenkins.getJobs();
								
								
								
								JobWithDetails job = jobs							
										.get("jenkis.test").details();
								
								HashMap <String,String> map = new HashMap<String,String>();
								
								map.put("token", "venkat");
								System.out.println(job.getName());
								
								List<Build> list = job.getBuilds();
								System.out.println(job.getNextBuildNumber());
								for(int i=0;i<list.size();i++) {
									System.out.println(list.get(i).getNumber());
								}
								JobWithDetails job1 = jenkins.getJob("jenkis.test");
								
								String url = "http://192.168.1.7:8080/jenkins/buildByToken/build?job=jenkis.test&token=venkat";

								URL obj = new URL(url);
								HttpURLConnection con = (HttpURLConnection) obj.openConnection();

								// optional default is GET
								con.setRequestMethod("GET");

								//add request header
								con.setRequestProperty("User-Agent", "\"Mozilla/5.0\";");

								int responseCode = con.getResponseCode();
								System.out.println("responseCode--->"+responseCode);
								
						        //QueueReference queueRef = job1.build(map, true);

								//job.build(map);
								
								//map.put("mail_id", "sizzlers.hack@gmail.com");
								//job.build();
								//job.build(map);
								
								
								
								ci.setDesc("Deployment Started");
								ci.setSpeech("Speech from api");
								ci.setDisplayText(""+job.getNextBuildNumber());
								ci.setSource(job.getUrl());
								
								Gson	gson1 = new Gson();
								
								responseOutput=gson1.toJson(ci);
								
								
								
								
						//	}
	    		        	  
	    		          } catch (Exception ex) {
	    		            ex.printStackTrace();
	    		          }

	    		        
	    	   } catch (Exception e) {
	    	        e.printStackTrace();
	    	        return "error--->"+e.getMessage();
	    	     } finally {
	    	         if (connection != null) {
	    	             connection.disconnect();
	    	         }
	    	     }
	        return responseOutput;
	   
	    
	}
	  
	  
	  
	    public String callApiAi(String inputRequest) {
	    	   try {
	    		   
	    		   AIConfiguration configuration = new AIConfiguration("1df2a5ba115a4e7d81f2c91396db2a00");

	    		    AIDataService dataService = new AIDataService(configuration);

	    		    String resp;
	    		    
	    		  

	    		          try {
	    		            AIRequest request = new AIRequest(inputRequest);
	    		            
	    		            AIServiceContext customContext = AIServiceContextBuilder.buildFromSessionId("566");

	    		            AIResponse response = dataService.request(request);

	    		            if (response.getStatus().getCode() == 200) {
	    		              System.out.println(response.getResult().getFulfillment().getSpeech());
	    		              System.out.println(response.getResult().getMetadata().getIntentName());
	    		              System.out.println(response.getResult().getResolvedQuery());
	    		              
	    		               resp = response.getResult().getFulfillment().getSpeech();
	    		            } else {
	    		              System.err.println(response.getStatus().getErrorDetails());
	    		            }
	    		          } catch (Exception ex) {
	    		            ex.printStackTrace();
	    		          }

	    		        
	    	   } catch (Exception e) {
	    	        e.printStackTrace();
	    	        return "error--->"+e.getMessage();
	    	     } finally {
	    	         if (connection != null) {
	    	             connection.disconnect();
	    	         }
	    	     }
	        return "Successfully get results from api.ai!";
	   
	    
	}
	  
	  

}
