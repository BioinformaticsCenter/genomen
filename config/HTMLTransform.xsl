<?xml version="1.0" encoding="ISO-8859-1"?>
<xsl:stylesheet version="2.0"
  xmlns:xhtml="http://www.w3.org/1999/xhtml" 
  xmlns="http://www.w3.org/1999/xhtml"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:xs="http://www.w3.org/2001/XMLSchema"
  exclude-result-prefixes="xhtml xsl xs"> 

<xsl:output method="xml" version="1.0" encoding="UTF-8" doctype-public="-//W3C//DTD XHTML 1.1//EN" doctype-system="http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd" indent="yes"/> 

<xsl:template match="/">
  <html>
    
    <head>
    <title>Analysis results</title>
       <style type="text/css">
		body {
			font-family: Verdana, Arial, Helvetica, sans-serif;
			font-size: 12px;
		}

		table {
			border-collapse:collapse;
		}
		th {
		  background-color:#0066FF;
		  color:white;
		}
		td {
			padding:5px;
		}
		table, td, th {		
			border: 1px solid black;		
		}
		div.trait {
			border: 1px solid black;
			padding: 10px;	
			margin: 20px 2% 20px 2%;			
		}
		div.individual {
			background-color:#E6F0FF;	
			border-style:solid;
			border-width:1px;
			padding: 10px;	
			margin: 20px 2% 20px 2%;			
		}		
        </style>
    </head>
  <body>
  <p>Created: <xsl:value-of select="report/@date"/></p>  
  
	<xsl:for-each select="report/individual">
		<div class="individual">
		<h2><xsl:value-of select="id"/></h2>
		<xsl:for-each select="traitTable/traitEntry">
			<div class="trait">
				<h3><b><xsl:value-of select="title/."/></b></h3>
				<p><xsl:value-of select="shortDescription/."/></p>	
				<p><xsl:value-of select="longDescription/."/></p>		
				  <table>
					<tr>
						<xsl:for-each select="table/headers/*">
							<th><xsl:value-of select ="."/></th>
						</xsl:for-each>
					</tr>
				  
					<xsl:for-each select="table/rows/row">
					<tr>
						<xsl:for-each select="*">
							<td><xsl:value-of select="."/></td>
						</xsl:for-each>
					</tr>
					</xsl:for-each>
				  </table>	
			</div>
		</xsl:for-each> 
	</div>
	</xsl:for-each> 
  </body>
  </html>
</xsl:template>

</xsl:stylesheet>