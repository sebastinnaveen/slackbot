package com.techgig.hubot.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.offbytwo.jenkins.JenkinsServer;
import com.offbytwo.jenkins.model.Build;
import com.offbytwo.jenkins.model.Job;
import com.offbytwo.jenkins.model.JobWithDetails;
import com.offbytwo.jenkins.model.QueueReference;
import com.techgig.hubot.model.ChatInformation;

import org.apache.http.HttpRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileReader;
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
import net.gpedro.integrations.slack.SlackAction;
import net.gpedro.integrations.slack.SlackActionType;
import net.gpedro.integrations.slack.SlackApi;
import net.gpedro.integrations.slack.SlackAttachment;
import net.gpedro.integrations.slack.SlackMessage;

@RestController
public class HubotController {
	HttpURLConnection connection = null;
	  @RequestMapping("/Slackbot")
	    public void slackbot(HttpServletRequest request) {
	    	   try {
	    		   
	    		   // api.ai check
	    		   
	    		  
	    		   
	    		   
	    	         // Create connection
	    	        
	    	         
	    	         
	    	        // if(request!=null&&request.getParameter("text")!=null&&(request.getParameter("text").contains("Deploy")||request.getParameter("text").contains("Install"))) {
	    	        	 String resp,email="";
	    	        	 System.getenv("VCAP_SERVICES");
	    	        	  File jsonDir= new ClassPathResource("Config.json").getFile();
    		        	  System.out.println(jsonDir.getCanonicalPath());
	    	        	  JsonParser parser = new JsonParser();
	    	        	  
    		        	  Object obj= parser.parse(new FileReader(jsonDir));
    		        	  
    		        	  
    		        	  JsonObject jsonObject=(JsonObject) obj;
    		        	  
    		        	  JsonObject jsonObject1=(JsonObject)jsonObject.get("approvals");
    		        	  
    		        	  String approvals = jsonObject1.get("app").getAsString();
    		        	  
    		        	  String name = jsonObject.get("name").getAsString();
    		        	  
    		        	  String chatInput = request.getParameter("text");
    		        	  
    		        	  String slackName = request.getParameter("user_name");
    		        	  System.out.println(slackName);
    		        	  System.out.println(chatInput);
    		        	  System.out.println(name);
    		        	    System.out.println(approvals);
    		        	    boolean accessFlag = false;
    		        	    String projectName="";
    		        	    if(slackName!=null&& slackName.equalsIgnoreCase(name) || slackName.equalsIgnoreCase("slackbot")) {
    		        	    	
    		        	    	 
//    	    		        		  
    	    		        		  String[] aclArray = approvals.split(",");
    	    		        		  System.out.println(aclArray.length);
    	    		        		  
    	    		        		  if(aclArray.length > 0) {
    	    		        			  
    	    		        			  for (int i=0;i<aclArray.length;i++) {
    	    		        				  System.out.println("inside loop"+aclArray[i].toLowerCase());
    	    		        				  System.out.println(chatInput.toLowerCase());
    	    		    		        	    System.out.println(chatInput.toLowerCase().indexOf(aclArray[i].toLowerCase()));
    	    		        				  if(chatInput.toLowerCase().indexOf(aclArray[i].toLowerCase()) >0) {
    	    		        					  accessFlag =true;
    	    		        					  projectName=aclArray[i].toLowerCase();
    	    		        					  break;
    	    		        				  }
    	    		        			  }
    	    		        		  }
    	    		        		  
    	    		        		  String apiInput=  callApiAi(slackName,chatInput);
    		        	    }
    		        	    else {
    		        	    	sendApprovalNotifications(slackName, "", approvals);
    		        	    }
    	    		        		  
	    	   //if(accessFlag) {
	    		

	    	   //}
    		        	    
    		        	    
    		        	    
    
    		        	  
    		        	  
//    		        	  for (int i=0;i<aclArray.length;i++) {
//	        				  
//	        				  if(acl.toLowerCase().equals(aclArray[i].toLowerCase())) {
//	        					  System.out.println(email);
//	        					  break;
//	        				  }
    		        	  
    		        	  
//    		        	  
    		        	//
//    		        		  if(aclArray.length > 0) {
//    		        			  
//    		        			  for (int i=0;i<aclArray.length;i++) {
//    		        				  
//    		        				  if(acl.toLowerCase().equals(aclArray[i].toLowerCase())) {
//    		        					  System.out.println(email);
//    		        					  break;
//    		        				  }
//    		        			  }
//    		        		  }
    		        		  
    		        //	  }
	    	     //    }
	    	         
	    	         
	    	     } catch (Exception e) {
	    	        e.printStackTrace();
	    	        //return "error--->"+e.getMessage();
	    	     } finally {
	    	         if (connection != null) {
	    	             connection.disconnect();
	    	         }
	    	     }
	      //  return "Successfully Posted in Slack!";
	   
	    
	}
	  
	  public String sendNotifications(String username,String chatinput,String project) {
		  String notiMsg="";
		  try {
			  final URL url = new URL("https://hooks.slack.com/services/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");
 	         connection = (HttpURLConnection) url.openConnection();
 	         connection.setRequestMethod("POST");
 	         connection.setConnectTimeout(5000);
 	         connection.setUseCaches(false);
 	         connection.setDoInput(true);
 	         connection.setDoOutput(true);
		  JsonObject obj1 = new JsonObject();
	         obj1.addProperty("channel", "#devops");
	         
	        
	         obj1.addProperty("username", username);
	         obj1.addProperty("text", chatinput);
	         System.out.println("orig jsonstring--->"+obj1.toString()+"   Username--->"+username);
	      String jsonpayload = "payload="+ URLEncoder.encode(obj1.toString(),"UTF-8");
System.out.println("orig jsonpayload--->"+jsonpayload);

//	         final String payload = "payload="
//	                 + URLEncoder.encode("{\"channel\":\"#devops\",\"username\":\"Sizzler-DevOps\",\"text\":\"Hubot message from Naveen #devops\",\"icon_emoji\":\":happy:\"}", "UTF-8");
//	         System.out.println("orig payload--->"+payload);
	         // Send request
	         final DataOutputStream wr = new DataOutputStream(
	                 connection.getOutputStream());
	         wr.writeBytes(jsonpayload);
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
	         notiMsg= response.toString();
		  }
		  catch(Exception e) {
			  e.printStackTrace();
			  return "error--->"+e.getMessage();
		  }
		  return notiMsg;
	  }
	  
	  public String sendApprovalNotifications(String username,String chatinput,String project) {
		  String notiMsg="";
		  try {
			  //final URL url = new URL("https://hooks.slack.com/services/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");
			  
			  SlackApi api = new SlackApi("https://hooks.slack.com/services/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");
			 // api.call(new SlackMessage("#workflowapproval", "Sizzler-DevOps", "DevOps workflow approval"));
			  SlackAttachment attach =new SlackAttachment();
			  
			  SlackAction action = new SlackAction("approve","Approve",SlackActionType.BUTTON, "Approve");
			  SlackAction action1 = new SlackAction("reject","Reject",SlackActionType.BUTTON, "Reject");
			  attach.setText("");
			  attach.setFallback("You are not clicked approve/reject");
			  attach.setCallbackId("wk");
			  attach.setColor("#3AA3E3");
			  attach.addAction(action);
			  attach.addAction(action1);
			  api.call(new SlackMessage("#workflowapproval", "Sizzler-DevOps", "DevOps workflow approval").addAttachments(attach));
			  api.call(new SlackMessage("#devops", "Sizzler-DevOps", "You are not authorized to trigger this command, hence request for approval routed to manager"));
// 	         connection = (HttpURLConnection) url.openConnection();
// 	         connection.setRequestMethod("POST");
// 	         connection.setConnectTimeout(5000);
// 	         connection.setUseCaches(false);
// 	         connection.setDoInput(true);
// 	         connection.setDoOutput(true);
//		  JsonObject obj1 = new JsonObject();
//		  
//		  
//			
//
//		  
//		  
//	         obj1.addProperty("channel", "#workflowapproval");
//	         
//	        
//	         obj1.addProperty("username", "Sizzler-DevOps");
//	         obj1.addProperty("text", "DevOps workflow approval");
//	         obj1.addProperty("attachments", " [\r\n" + 
//	         		"       {\r\n" + 
//	         		"           \"text\": \"\",\r\n" + 
//	         		"           \"fallback\": \"You are not clicked approve/reject\",\r\n" + 
//	         		"           \"callback_id\": \"wk\",\r\n" + 
//	         		"           \"color\": \"#3AA3E3\",\r\n" + 
//	         		"           \"attachment_type\": \"default\",\r\n" + 
//	         		"           \"actions\": [\r\n" + 
//	         		"               {\r\n" + 
//	         		"                   \"name\": \"approve\",\r\n" + 
//	         		"                   \"text\": \"Approve\",\r\n" + 
//	         		"                   \"type\": \"button\",\r\n" + 
//	         		"                   \"value\": \"Approve\"\r\n" + 
//	         		"               },\r\n" + 
//	         		"               {\r\n" + 
//	         		"                   \"name\": \"reject\",\r\n" + 
//	         		"                   \"text\": \"Reject\",\r\n" + 
//	         		"                   \"type\": \"button\",\r\n" + 
//	         		"                   \"value\": \"Reject\"\r\n" + 
//	         		"               }\r\n" + 
//	         		"                             \r\n" + 
//	         		"           ]\r\n" + 
//	         		"       }\r\n" + 
//	         		"   ]");
//	         System.out.println("orig jsonstring--->"+obj1.toString()+"   Username--->"+username);
//	      String jsonpayload = "payload="+ URLEncoder.encode(obj1.toString(),"UTF-8");
//System.out.println("orig jsonpayload--->"+jsonpayload);
//
//	         final String payload = "payload="
//	                 + URLEncoder.encode("{\"channel\":\"#workflowapproval\",\"username\":\"Sizzler-DevOps\",\"text\":\"Hubot message from Naveen #devops\",\"icon_emoji\":\":happy:\"}", "UTF-8");
//	         System.out.println("orig payload--->"+payload);
//	         // Send request
//	         final DataOutputStream wr = new DataOutputStream(
//	                 connection.getOutputStream());
//	         wr.writeBytes(jsonpayload);
//	         wr.flush();
//	         wr.close();
//	        // System.out.println(sampleProperty.getStringProp1());
//	         // Get Response
//	         final InputStream is = connection.getInputStream();
//	         final BufferedReader rd = new BufferedReader(new InputStreamReader(is));
//	         String line;
//	         StringBuilder response = new StringBuilder();
//	         while ((line = rd.readLine()) != null) {
//	             response.append(line);
//	             response.append('\n');
//	         }
//
//	         rd.close();
//	         notiMsg= response.toString();
		  }
		  catch(Exception e) {
			  e.printStackTrace();
			  return "error--->"+e.getMessage();
		  }
		  return notiMsg;
	  }
	  
	  
	  @RequestMapping("/triggerjenkins")
	    public String triggerJenkins2() throws ParseException, IOException{
		  String responsestr,responseOutput="",gson,reqGson="";
	    	   try {
	    		   
	    		   ChatInformation ci = new ChatInformation();
	    		          try {
	    		        	  
	    		        	  JenkinsServer jenkins=null;
	    		        	  
	    		    			
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
								
								
								sendNotifications("sebasmtech", "Build Completed Successfully" ,"");
								
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
	    
	    public String triggerJenkins1() throws ParseException, IOException{
			  String responsestr,responseOutput="",gson,reqGson="";
		    	   try {
		    		   
		    		   ChatInformation ci = new ChatInformation();
		    		          try {
		    		        	  
		    		        	  JenkinsServer jenkins=null;
		    		        	  
		    		    			
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
	  
	  
	  
	    public String callApiAi(String username,String inputRequest) {

		    String resp="";
		    
		  

	    	   try {
	    		   
	    		   AIConfiguration configuration = new AIConfiguration("1df2a5ba115a4e7d81f2c91396db2a00");

	    		    AIDataService dataService = new AIDataService(configuration);

	    		          try {
	    		            AIRequest request = new AIRequest(inputRequest);
	    		            
	    		            AIServiceContext customContext = AIServiceContextBuilder.buildFromSessionId("566");

	    		            AIResponse response = dataService.request(request);

	    		            if (response.getStatus().getCode() == 200) {
	    		              System.out.println(response.getResult().getFulfillment().getSpeech());
	    		              System.out.println(response.getResult().getMetadata().getIntentName());
	    		              System.out.println(response.getResult().getResolvedQuery());
	    		              String intent = response.getResult().getAction();
	    		              boolean action = response.getResult().isActionIncomplete();
	    		               resp = response.getResult().getFulfillment().getSpeech();
	    		               System.out.println("intent---->"+intent);
	    		               if(!action && intent.equalsIgnoreCase("smalltalk.greetings.hello"))
	    		               {
	    		            	   
	    		            	   sendNotifications(username, resp ,"");
	    		               }
	    		               else if(action)
	    		               {
	    		            	   
	    		            	   sendNotifications(username, resp ,"");
	    		               }
	    		               else
	    		               {
	    		            	   triggerJenkins1();
	    		            	   sendNotifications(username, resp ,"");
	    			    		   
	    		               }
	    		               
	    		               
	    		               
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
	        return resp;
	   
	    
	}
	  
	  

}
