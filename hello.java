import org.json.simple.JSONObject;
import java.net.HttpURLConnection;
import java.net.URL;
import java.io.*;
import java.util.*;
import org.json.simple.JSONArray;
import org.json.simple.parser.ParseException;
import org.json.simple.parser.JSONParser;

class hello 
{

   public static void main(String[] args) throws Exception 
   {
	   String towrite;
	   String text="";
	   FileWriter fw = new FileWriter("cric.csv");
  	   towrite ="innings,over,Bowlername,Batsmanname,runs,wicket,description,nh/h,wickettype,wickethelper,extras,ballspeed(kph)";
	   for (int j = 0; j < towrite.length(); j++) 
	   {
	       fw.write(towrite.charAt(j));
	   }
	   fw.write('\n');
	   for(int innings=0;innings<2;innings++)
	   {
		   for(int page=0;page<15;page++)
		   {
			   text="";
			   URL url = new URL("http://site.web.api.espn.com/apis/site/v2/sports/cricket/18902/playbyplay?contentorigin=espn&event=1157755&page="+(page+1)+"&period="+(innings+1)+"&section=cricinfo");
			   HttpURLConnection connection = (HttpURLConnection)url.openConnection();
			   connection.setRequestMethod("GET");
			   connection.connect();
			   Scanner sc = new Scanner(url.openStream());
				   while(sc.hasNext())
				   {
					   text=text+sc.nextLine();
				   }
		      JSONParser parser = new JSONParser();
				
		      try{
		         Object obj = parser.parse(text);
		         JSONObject jo =(JSONObject) obj;
		         JSONObject jo1=(JSONObject) jo.get("commentary");
		         JSONArray items=(JSONArray) jo1.get("items");
		         
		         for(int i=0;i<items.size();i++)
		         {
		        	 towrite="";
		        	 JSONObject jo2=(JSONObject) items.get(i);
		        	 JSONObject jo3=(JSONObject) jo2.get("batsman");
		        	 JSONObject jo4=(JSONObject) jo3.get("athlete");
		        	 JSONObject jo5=(JSONObject) jo2.get("bowler");
		        	 JSONObject jo6=(JSONObject) jo5.get("athlete");
		        	 JSONObject jo7=(JSONObject) jo2.get("dismissal");
		        	 JSONObject jo8=(JSONObject) jo2.get("over");
		        	 JSONObject jo9=(JSONObject) jo2.get("playType");
		        	 
			        	 if(jo6.toString().equals("{}"))
			        	 {}
			        	 else
			        	 {
			             towrite=towrite+String.valueOf(jo2.get("period"))+","+String.valueOf(jo8.get("actual"))+","+(String)jo6.get("displayName")+","+(String)jo4.get("displayName")+","+String.valueOf(jo2.get("scoreValue"))+","+String.valueOf(jo7.get("dismissal")+",");
			             String textString=(String)jo2.get("text");
			             
			             if(textString!=null)
			             {
				             for(int j=0;j<textString.length();j++)
				             {
				            	 if(textString.charAt(j)!=',')
				            	 {
				            		 towrite=towrite+textString.charAt(j);
				            	 }
				            	 else
				            	 {
				            		 towrite=towrite+" ";
				            	 }
				             }
			             }
			             
				         if(jo9.get("description").equals("four")||jo9.get("description").equals("six"))
				         {
				        	 towrite=towrite+","+"home run";
				         }
				         else
				         {
				        	 towrite=towrite+","+"non-home run";
				         }
			        	 
			        	 if(jo7.get("dismissal").equals(false))
			        	 {
			        		 towrite=towrite+","+"no wicket";
			        	 }
			        	 else
			        	 {
			        		 towrite=towrite+","+jo7.get("type");
			        	 }
			        	 
			        	 if(jo7.get("dismissal").equals(false))
			        	 {
			        		 towrite=towrite+","+"no wicket";
			        	 }
			        	 else
			        	 {
			        		 int flag=0;
			        		 
			        		 if(jo7.get("type").equals("caught"))
			        		 {
			        			   JSONArray jo10=(JSONArray) jo2.get("athletesInvolved");
			        			   for(int f=0;f<3;f++)
			        			   {
			        			   JSONObject jo11=(JSONObject) jo10.get(f);
			        			   if(jo11.get("displayName").equals(jo4.get("displayName"))||jo11.get("displayName").equals(jo6.get("displayName")))
			        			   {}
			        			   else
			        			   {
			        				   towrite=towrite+","+jo11.get("displayName");   
			        			   }
			        			   }
			        			   flag=1;
			        		  }
			        		   
			        		   if(jo7.get("type").equals("run out"))
			        		   {
			        			   String[] strings =((String)jo7.get("text")).split(" ",-1);
			        			     for(int g=0;g<strings.length;g++)
			        			     {
			        			    	 if(strings[g].equals("out"))
			        			    	 {
			        			    		 towrite=towrite+","+strings[g+1];
			        			    		 flag=1;
			        			    		 break;
			        			    	 }
			        			     }
			        		   }
			        		   
			        		   if(jo7.get("type").equals("stumped"))
			        		   {
			        			   JSONArray jo10=(JSONArray) jo2.get("athletesInvolved");
			        			   for(int f=0;f<3;f++)
			        			   {
				        			   JSONObject jo11=(JSONObject) jo10.get(f);
				        			   if(jo11.get("displayName").equals(jo4.get("displayName"))||jo11.get("displayName").equals(jo6.get("displayName")))
				        			   {}
				        			   else
				        			   {
				        				   towrite=towrite+","+jo11.get("displayName");   
				        			   }
			        			   }
			        			   flag=1;
			        		   }
			        		   
			        		   if(flag==0)
			        		   {
			        			   towrite=towrite+",";
			        		   }  
			        	 }
			        	 
			        	 if(jo9.get("description").equals("four")||jo9.get("description").equals("six")||jo9.get("description").equals("run")||jo9.get("description").equals("no run")||jo9.get("description").equals("out"))
			        	 {
			        		 towrite=towrite+","+"no extra";
			        	 }
			        	 else
			        	 {
			        		 towrite=towrite+","+jo9.get("description");
			        	 }
			        		
			        	 towrite=towrite+","+jo2.get("speedKPH");
			    
			             if(String.valueOf(jo8.get("actual")).equals("null"))
			        	 {}
			        	 else
			        	 {
			        	 for (int j = 0; j < towrite.length(); j++) 
				            {
				                fw.write(towrite.charAt(j));
				            }
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
	  fw.close();
	  System.out.println("Done"); 
   }
}