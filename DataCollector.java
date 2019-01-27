import org.json.simple.JSONObject;
import java.net.HttpURLConnection;
import java.net.URL;
import java.io.*;
import java.util.*;
import org.json.simple.JSONArray;
import org.json.simple.parser.ParseException;
import org.json.simple.parser.JSONParser;

class DataCollector
{
   public static void main(String[] args) throws Exception 
   {
		String str;
		long seriesId=1;
		int noofMatches;
		ArrayList<Long> matchesId= new ArrayList<>();
		URL seriesUrl = new URL("http://www.espncricinfo.com/ci/engine/match/index/series.html?series=12220");
		HttpURLConnection conn = (HttpURLConnection)seriesUrl.openConnection();
	    conn.setRequestMethod("GET");
	    conn.connect();
	    Scanner sc = new Scanner(seriesUrl.openStream());
	    ArrayList<String> links=new ArrayList<>();
	    while(sc.hasNext())
	    {
	   	  str=sc.nextLine();
		  if(str.matches("  <section class=\"default-match-block \" data-matchstatus=\"complete\">"))
		  {
			  int flag=0;
			  while(flag==0)
			  {
				 if(sc.hasNext())
				 {
			   	    str=sc.nextLine();
				    if(str.startsWith("     <a href"))
			        {
					   links.add(str);
					   flag=1;
		  		    }
			      }
			   }
			}
		  }

	   for(int i=0;i<links.size();i++)
	   {
		  String[] strings=links.get(i).split("/",-1);
		  seriesId=Long.valueOf(strings[4]);
		  matchesId.add(Long.valueOf(strings[6]));
	   }
	   
	   noofMatches=matchesId.size();
	   
	   for(int matches=0;matches<noofMatches;matches++)
	   {
	   String toWrite;
	   String text;
	   long pageCount;
	   FileWriter fw = new FileWriter(matchesId.get(matches)+".csv");
  	   toWrite ="innings,over,Bowlername,Batsmanname,runs,wicket,description,nh/h,wickettype,wickethelper,extras,ballspeed(kph)";
	   // Look at how to write entire string to file instead of one character at a time
  	   fw.write(toWrite);
  	   fw.write('\n');
	   for(int innings=0;innings<2;innings++)
	   {
		   text="";
		   URL url = new URL("http://site.web.api.espn.com/apis/site/v2/sports/cricket/"+seriesId+"/playbyplay?contentorigin=espn&event="+matchesId.get(matches)+"&page=1&period="+(innings+1)+"&section=cricinfo");
		   HttpURLConnection connection = (HttpURLConnection)url.openConnection();
		   connection.setRequestMethod("GET");
		   connection.connect();
		   sc = new Scanner(url.openStream());
		   while(sc.hasNext())
		   {
			   text=text+sc.nextLine();
		   }
	       JSONParser parser = new JSONParser();
	       Object object=parser.parse(text);
	       JSONObject JSONPageCount =(JSONObject) object;
	       JSONObject JSONPageCount1=(JSONObject) JSONPageCount.get("commentary");
	       String temp=String.valueOf(JSONPageCount1.get("pageCount"));
	       pageCount=Long.valueOf(temp);
	       
		   for(int page=0;page<pageCount;page++) // use pageCount instead of an arbitary number like 15.
		   {
			   text="";
			   url = new URL("http://site.web.api.espn.com/apis/site/v2/sports/cricket/"+seriesId+"/playbyplay?contentorigin=espn&event="+matchesId.get(matches)+"&page="+(page+1)+"&period="+(innings+1)+"&section=cricinfo");
			   connection = (HttpURLConnection)url.openConnection();
			   connection.setRequestMethod("GET");
			   connection.connect();
			   sc = new Scanner(url.openStream());
			   while(sc.hasNext()) // adjust indent
			   {
				   text=text+sc.nextLine();
			   }
				
		       try
		       {
		         Object obj = parser.parse(text);
		         JSONObject mainJSON =(JSONObject) obj;
		         JSONObject commentaryJSON=(JSONObject) mainJSON.get("commentary");
		         JSONArray items=(JSONArray) commentaryJSON.get("items");
		         
		         for(int i=0;i<items.size();i++)
		         {
		        	 toWrite="";
		        	 JSONObject ballJSON=(JSONObject) items.get(i); // use proper names instead of "jo"
		        	 JSONObject batsmanJSON=(JSONObject) ballJSON.get("batsman");
		        	 JSONObject batsmanAthleteJSON=(JSONObject) batsmanJSON.get("athlete");
		        	 JSONObject bowlerJSON=(JSONObject) ballJSON.get("bowler");
		        	 JSONObject bowlerAthleteJSON=(JSONObject) bowlerJSON.get("athlete");
		        	 JSONObject dismissalJSON=(JSONObject) ballJSON.get("dismissal");
		        	 JSONObject overJSON=(JSONObject) ballJSON.get("over");
		        	 JSONObject playtypeJSON=(JSONObject) ballJSON.get("playType");
		        	 
			        	 if(!bowlerAthleteJSON.toString().equals("{}")) // Check for notEquals
			        	 {
			             toWrite=toWrite+String.valueOf(ballJSON.get("period"))+","+String.valueOf(overJSON.get("actual"))+","+(String)bowlerAthleteJSON.get("displayName")+","+(String)batsmanAthleteJSON.get("displayName")+","+String.valueOf(ballJSON.get("scoreValue"))+","+String.valueOf(dismissalJSON.get("dismissal"))+",";
			             String textString=(String)ballJSON.get("text");
			             
			             if(textString!=null)
			             {
					     // do not test for ',' character by character. Use string function in Java
			            	 
			            	 textString=textString.replace(',',' ');
			            	 textString=textString.replaceAll("<b>","");
			            	 textString=textString.replaceAll("</b>","");
			            	 textString=textString.replaceAll("<B>","");
			            	 textString=textString.replaceAll("</B>","");
			            	 textString=textString.replaceAll("<strong>","");
			            	 textString=textString.replaceAll("</strong>","");
			            	 toWrite=toWrite+textString;
			            	 
			             }
			             
				         if(playtypeJSON.get("description").equals("four")||playtypeJSON.get("description").equals("six"))
				         {
				        	 toWrite=toWrite+","+"home run";
				         }
				         else
				         {
				        	 toWrite=toWrite+","+"non-home run";
				         }
			        	 
			        	 if(dismissalJSON.get("dismissal").equals(false))
			        	 {
			        		 toWrite=toWrite+","+"no wicket";
			        	 }
			        	 else
			        	 {
			        		 toWrite=toWrite+","+dismissalJSON.get("type");
			        	 }
			        	 
			        	 if(dismissalJSON.get("dismissal").equals(false))
			        	 {
			        		 toWrite=toWrite+","+"no wicket";
			        	 }
			        	 else
			        	 {
			        		 int flag=0;
			        		 // do this: comment tring to retrieve bowler name, batsman name
			        		 //retrieving the fielder's name
			        		 if(dismissalJSON.get("type").equals("caught"))
			        		 {
			        			 int check=0;
			        			   JSONArray jo10=(JSONArray) ballJSON.get("athletesInvolved");
			        			   for(int f=0;f<jo10.size();f++)
			        			   {
			        			   JSONObject jo11=(JSONObject) jo10.get(f);
			        			   if(!jo11.get("displayName").equals(batsmanAthleteJSON.get("displayName"))&&!jo11.get("displayName").equals(bowlerAthleteJSON.get("displayName")))
			        			   {
			        				   toWrite=toWrite+","+jo11.get("displayName");
			        				   check=1;
			        			   }
			        			   }
			        			   if(check==0)
			        			   {
			        				   toWrite=toWrite+","+" ";
			        			   }
			        			   flag=1;
			        		  }
			        		 
			        		  //retrieving the fielder's name responsible for runout 
			        		  if(dismissalJSON.get("type").equals("run out"))
			        		  {
			        			   String[] strings =((String)dismissalJSON.get("text")).split(" ",-1);
			        			     for(int g=0;g<strings.length;g++)
			        			     {
			        			    	 if(strings[g].equals("out"))
			        			    	 {
			        			    		 toWrite=toWrite+","+strings[g+1];
			        			    		 flag=1;
			        			    		 break;
			        			    	 }
			        			     }
			        		   }
			        		   
			        		  //retrieving the wicketkeeper's name
			        		   if(dismissalJSON.get("type").equals("stumped"))
			        		   {
			        			   JSONArray jo10=(JSONArray) ballJSON.get("athletesInvolved");
			        			   for(int f=0;f<3;f++)
			        			   {
				        			   JSONObject jo11=(JSONObject) jo10.get(f);
				        			   if(!jo11.get("displayName").equals(batsmanAthleteJSON.get("displayName"))&&!jo11.get("displayName").equals(bowlerAthleteJSON.get("displayName")))
				        			   {
				        				   toWrite=toWrite+","+jo11.get("displayName");   
				        			   }
			        			   }
			        			   flag=1;
			        		   }
			        		   
			        		   if(flag==0)
			        		   {
			        			   toWrite=toWrite+",";
			        		   }
			        	 }
			        	 
			        	 if(playtypeJSON.get("description").equals("four")||playtypeJSON.get("description").equals("six")||playtypeJSON.get("description").equals("run")||playtypeJSON.get("description").equals("no run")||playtypeJSON.get("description").equals("out"))
			        	 {
			        		 toWrite=toWrite+","+"no extra";
			        	 }
			        	 else
			        	 {
			        		 toWrite=toWrite+","+playtypeJSON.get("description");
			        	 }
			        		
			        	 toWrite=toWrite+","+ballJSON.get("speedKPH");
			    
			             if(String.valueOf(overJSON.get("actual")).equals("null"))
			        	 {}
			        	 else
			        	 {
			        		fw.write(toWrite);
				            fw.write('\n');
			        	 }
		         }
		         }
		        
		      }catch(ParseException pe) {
				
		         System.out.println("position: " + pe.getPosition());
		         System.out.println(pe);
		      }
		  }
	  }
	  System.out.println("Matches over: "+matches);
      fw.close();
	  }
	  System.out.println("Done");
   }
}
