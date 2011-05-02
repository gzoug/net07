<?xml version="1.0" encoding="utf-8"?>
<!DOCTYPE xsl:stylesheet [
<!ENTITY laquo  "&#171;">
<!ENTITY copy   "&#169;">
<!ENTITY nbsp   "&#160;">
<!ENTITY raquo  "&#187;">
<!ENTITY sect   "&#167;">
]>   
<xsl:stylesheet 
   xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
   xmlns:exslt="http://exslt.org/common" 
   xmlns:math="http://exslt.org/math"
   xmlns:date="http://exslt.org/dates-and-times" 
   xmlns:func="http://exslt.org/functions" 
   xmlns:set="http://exslt.org/sets" 
   xmlns:str="http://exslt.org/strings" 
   xmlns:dyn="http://exslt.org/dynamic" 
   xmlns:saxon="http://icl.com/saxon" 
   xmlns:xalanredirect="org.apache.xalan.xslt.extensions.Redirect" 
   xmlns:xt="http://www.jclark.com/xt" 
   xmlns:libxslt="http://xmlsoft.org/XSLT/namespace" 
   xmlns:test="http://xmlsoft.org/XSLT/" 
   xmlns:xi="http://www.w3.org/2001/XInclude"
   xmlns="http://www.w3.org/1999/xhtml"
   version="1.0" 
   extension-element-prefixes="exslt math date func set str dyn saxon xalanredirect xt libxslt test"
   exclude-result-prefixes="math str">


<xsl:output method="xml"
            version="1.0"
            doctype-public="-//W3C//DTD XHTML 1.0 Transitional//EN"
            doctype-system="http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd"
            standalone="no"
            indent="yes"
            encoding="utf-8"
            omit-xml-declaration="no"
/>
	<xsl:key name="exercises_key" match="results" use="@exercise-id"/>
	<xsl:variable name='distinct_exercises' select="//results[generate-id(@exercise-id)=generate-id(key('exercises_key',@exercise-id)[1]/@exercise-id)]"/>

	<xsl:key name="students_key"  match="results" use="@student"/>
	<xsl:variable name='distinct_students'  select="//results[generate-id(@student)=generate-id(key('students_key',@student)[1]/@student)]"/>
				  
	
	
	<xsl:template match="/">
		<html lang="el" xml:lang="el">
			<head>
				<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
			</head>
		<body>
		<style>
		<xsl:text>
		body {
		  margin-top:0px;
		  margin-left:0px;
		  margin-right:0px;
		  font-family: tahoma, verdana;
		  border-top: 10px solid #800080;
          font-size:12px;
		  color:#404040;
		  padding:10px;
		}
		
		H1 {
		  font-weight:bold; font-size:18px;
		}
		H1.appname {
		  color:#800080;
		  margin:0px;
		  font-size:24px;
		}

		.pagefooter {
		   margin-top:20px;
		   border-top: 1px solid #400040;
		   font-size:12px;
		   color:#404040;
		   
		}
		
		
		.reporttable {
		   border:1px solid #a0a0a0;
		   font-size:14px;
		   padding:0px;
		}
        .reporttable .aa {
		  background-color:#e0e0e0;
          border:1px solid #a0a0a0;
		  font-weight: bold;
		  text-align:center;
		}
		.reporttable .am {
		  background-color:#f0f0f0;
          border:1px solid #a0a0a0;
		  font-weight: bold;
		}

		.reporttable .grade {
          border:1px solid #a0a0a0;
		}
		
		.reporttable .header {
          border:1px solid #a0a0a0;
		  background-color: #808080;
		  color:#ffffff;
		  font-weight:bold;
		  text-align:center;
		}
		.date {
		  color:#808080;
		  font-size:12px;
		}
		.results {
		  color:#000000;
		  font-size:12px;
		  text-align:center;
		  font-weight:bold;
		}

		</xsl:text>
		</style>
<h1 class="appname">Net07</h1>
<h1>Αναφορά παράδοσης ασκήσεων</h1>
Τελευταία ενημέρωση:
<xsl:call-template name="display-date">
<xsl:with-param name="date" select="//db/@date"/>
<xsl:with-param name="seconds">yes</xsl:with-param>
</xsl:call-template>		
		<table class="reporttable">
		   <tr class="header">
                     <td class="header">A/A</td>
		     <td class="header">A.M.</td>
			 <xsl:for-each select="$distinct_exercises">
			    <xsl:sort order="ascending" data-type="number" case-order="upper-first" select="@exercise-id"/>
				<td class="header"><xsl:value-of select="@exercise-id"/></td>
			 </xsl:for-each>			  
		   </tr>
		   <xsl:call-template name="parseResults"/>
		</table>

		<div class="pagefooter">
Net07 v1.2-2008 &nbsp;&#169; Τμήμα Διοικητικής Επιστήμης και Τεχνολογίας - Οικονομικό Πανεπιστήμιο Αθηνών
<br/>Υλοποίηση εφαρμογής: Βασίλιος Καρακόϊδας - Γεώργιος Μ. Ζουγανέλης
		</div>
		</body>
		</html>
	</xsl:template>
	
	<xsl:template name="parseResults">

		<xsl:for-each select="$distinct_students">
			<xsl:sort order="ascending" data-type="number" case-order="upper-first" select="@student"/>
   	        <xsl:variable name="sid" select="@student"/>
			<tr>
                           <td class="aa"><xsl:value-of select="position()"/></td>
			   <td class="am"><xsl:value-of select="@student"/></td>

     			   <xsl:for-each select="$distinct_exercises">
						<xsl:sort order="ascending" data-type="number" case-order="upper-first" select="@exercise-id"/>
				        
						<xsl:variable name="exid" select="@exercise-id"/>						
						<xsl:variable name="subcount" select="count(//results[(@exercise-id=$exid) and (@student=$sid)])"/>
						<xsl:variable name="result" select="//results[(@exercise-id=$exid) and (@student=$sid)]"/>
						<xsl:variable name="answers" select="count($result/answer)"/>
						<xsl:variable name="correct" select="count($result/answer[@correct='true'])"/>
						
						<!-- <xsl:variable name="correct" select="count(//exercise[@id=$exid]/question[@id=]/answer[@correct='true'] )"/>  -->
						<!-- <xsl:variable name="correct" select="count($result//answer[@answer-id=//exercise[@id=$exid]//answer[@correct='true']/@id] )"/> -->
			            
						<td class="grade">				   
						   <xsl:choose>
								<xsl:when test="$subcount &gt; 0">
								   <div class="date">
								     <xsl:call-template name="display-date">
								       <xsl:with-param name="date" select="$result/@submited"/>
								     </xsl:call-template>
								   </div>
								   <div class="results">
									 <xsl:choose>
									    <xsl:when test="$answers &gt; 0">OK
										 ( <xsl:value-of select="$correct"/> / <xsl:value-of select="$answers"/>)
										 </xsl:when>
										<xsl:otherwise>OK</xsl:otherwise>
									 </xsl:choose>
									</div>
								</xsl:when>
								<xsl:otherwise> </xsl:otherwise>
						   
						   </xsl:choose>
						</td>
			        </xsl:for-each>

			   
			</tr>
        </xsl:for-each>

		
	</xsl:template>

	
	
    <xsl:template name="display-date">
	   <xsl:param name="date"/>
	   <xsl:param name="seconds"/>
	   <xsl:if test="$date!=''">
	     <xsl:value-of select="substring($date,7,2)"/>/<xsl:value-of select="substring($date,5,2)"/>/<xsl:value-of select="substring($date,1,4)"/>,
	     <xsl:value-of select="substring($date,10,2)"/>:<xsl:value-of select="substring($date,12,2)"/>
	     <xsl:if test="$seconds='yes'">:<xsl:value-of select="substring($date,14,2)"/></xsl:if>
	   </xsl:if>
	</xsl:template>	
	
	
	
	
</xsl:stylesheet>
