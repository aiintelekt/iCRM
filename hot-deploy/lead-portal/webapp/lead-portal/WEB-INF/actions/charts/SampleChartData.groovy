import org.ofbiz.base.util.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
delegator = request.getAttribute("delegator");

// Bar or Line Chart Data preparation
JSONArray xAxisData = new JSONArray();
JSONArray yAxisData = new JSONArray();

xAxisData.add("Mon");
xAxisData.add("Tue");
xAxisData.add("Wed");
xAxisData.add("Thu");
xAxisData.add("Fri");
xAxisData.add("Sat");
xAxisData.add("Sun");

yAxisData.add(120);
yAxisData.add(200);
yAxisData.add(150);
yAxisData.add(80);
yAxisData.add(70);
yAxisData.add(110);
yAxisData.add(130);



context.put("xAxisData", xAxisData.toString());
context.put("yAxisData", yAxisData.toString());

//Pie Data preparation chart
JSONArray pieChartData = new JSONArray();

JSONObject piechart1=new JSONObject();
piechart1.put("name","Prabhakar")
piechart1.put("value","820")
pieChartData.add(piechart1);
JSONObject piechart2=new JSONObject();
piechart2.put("name","Suresh")
piechart2.put("value","900")
pieChartData.add(piechart2);
JSONObject piechart3=new JSONObject();
piechart3.put("name","Mahesh");
piechart3.put("value","520");
pieChartData.add(piechart3);
JSONObject piechart4=new JSONObject();
piechart4.put("name","Harish")
piechart4.put("value","400")
pieChartData.add(piechart4);

JSONObject piechart5=new JSONObject();
piechart5.put("name","Naresh")
piechart5.put("value","600")
pieChartData.add(piechart5);

context.put("pieChartData", pieChartData.toString());
