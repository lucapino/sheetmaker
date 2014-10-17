
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:msxsl="urn:schemas-microsoft-com:xslt" xmlns:myjs="urn:custom-javascript" exclude-result-prefixes="msxsl myjs">
  <xsl:output method="xml" version="1.0" encoding="UTF-8" indent="yes"/>
  <xsl:variable name="FilePath" select="string(//tokens/token[@name='%TITLEPATH%'])"/>
  <xsl:variable name="FolderJpgName" select="string('folder.jpg')"/>
  <xsl:variable name="FileName" select="string(//tokens/token[@name='%MOVIEFILENAME%'])"/>
  <xsl:variable name="TemplatePath" select="string(//tokens/token[@name='%PATH%'])"/>
  <xsl:variable name="RATING" select="string(//tokens/token[@name='%RATING%'])"/>
  <xsl:variable name="Studios" select="string(//tokens/token[@name='%STUDIOS%'])"/>
  <xsl:variable name="Runtime" select="string(//tokens/token[@name='%RUNTIME%'] * 60)"/>
  <xsl:variable name="DurationSec" select="string(//tokens/token[@name='%DURATIONSEC%'])"/>
  <!--xsl:variable name="Runtime" select="string(21 * 60)"/>
  <xsl:variable name="DurationSec" select="string('1265')"/-->
  
  <msxsl:script language="JavaScript" implements-prefix="myjs">
    <![CDATA[
	 
	  function getSourceFlags(oTemplatePath, oFilePath, oFileName)
    {
      var path='';
      var name = oFilePath.toLowerCase() + '\\' + oFileName.toLowerCase();
      if (name.indexOf("bluray") != -1)
      {
        path = oTemplatePath + '\\..\\Common\\SourceFlags\\source_bluray.png';
      }
      else if (name.indexOf("bdrip") != -1)
      {
        path = oTemplatePath + '\\..\\Common\\SourceFlags\\source_bluray.png';
       }
      else if (name.indexOf("video_ts") != -1)
      {
        path = oTemplatePath + '\\..\\Common\\SourceFlags\\source_dvd.png';
      }
	  else if (name.indexOf(".iso") != -1)
      {
        path = oTemplatePath + '\\..\\Common\\SourceFlags\\iso.png';
      }
      else if (name.indexOf("web-dl") != -1)
      {
        path = oTemplatePath + '\\..\\Common\\SourceFlags\\source_webdl.png';
      }
      else if (name.indexOf("r5") != -1)
      {
        path = oTemplatePath + '\\..\\Common\\SourceFlags\\source_r5.png';
      }
      else if (name.indexOf("dvdrip") != -1)
      {
        path = oTemplatePath + '\\..\\Common\\SourceFlags\\source_dvd.png';
      }
      else if (name.indexOf("dvdscr") != -1)
      {
        path = oTemplatePath + '\\..\\Common\\SourceFlags\\source_dvdscr.png';
      }
      else if (name.indexOf("dvd") != -1)
      {
        path = oTemplatePath + '\\..\\Common\\SourceFlags\\source_dvd.png';
      }
      else if (name.indexOf("hdtv") != -1)
      {
        path = oTemplatePath + '\\..\\Common\\SourceFlags\\source_hdtv.png';
      }
      else
      {
        path = oTemplatePath + '\\..\\Common\\SourceFlags\\generic.png';
      }
      return path;
    }
	
	  function getBoxes(oTemplatePath, oFilePath, oFileName)
    {
      var path='';
      var name = oFilePath.toLowerCase() + '\\' + oFileName.toLowerCase();
      if (name.indexOf("bluray") != -1)
      {
        path = oTemplatePath + '\\..\\Common\\Boxes\\bluray.png';
      }
      else if (name.indexOf("bdrip") != -1)
      {
        path = oTemplatePath + '\\..\\Common\\Boxes\\bdrip.png';
	  }
      else if (name.indexOf("video_ts") != -1)
      {
        path = oTemplatePath + '\\..\\Common\\Boxes\\dvd.png';	
      }
      else if (name.indexOf("web-dl") != -1)
      {
        path = oTemplatePath + '\\..\\Common\\Boxes\\webdl.png';
      }
      else if (name.indexOf("r5") != -1)
      {
        path = oTemplatePath + '\\..\\Common\\Boxes\\r5.png';
      }
      else if (name.indexOf("dvdrip") != -1)
      {
        path = oTemplatePath + '\\..\\Common\\Boxes\\dvd.png';
      }
      else if (name.indexOf("dvdscr") != -1)
      {
        path = oTemplatePath + '\\..\\Common\\Boxes\\dvd.png';
      }
      else if (name.indexOf("dvd") != -1)
      {
        path = oTemplatePath + '\\..\\Common\\Boxes\\dvd.png';
      }
      else if (name.indexOf("hdtv") != -1)
      {
        path = oTemplatePath + '\\..\\Common\\Boxes\\hdtv.png';
      }
      else
      {
        path = oTemplatePath + '\\..\\Common\\Boxes\\generic.png';
      }
      return path;
    }    
    
		    function getTime(oDurationSec, oRuntime, oReq)
    {
      var hours=0;
      var minutes=0;
      var seconds=0;
      var output=0;
     
      if (oDurationSec == 0)
      {
        hours = Math.floor(oRuntime/3600);
        minutes = Math.floor((oRuntime%3600)/60);
        seconds = Math.floor((oRuntime%3600)%60);
      }
      else
      {
        hours = Math.floor(oDurationSec/3600);
        minutes = Math.floor((oDurationSec%3600)/60);
        seconds = Math.floor((oDurationSec%3600)%60);
      }
     
      if(oReq == "hours")
      {
        output = hours;
      }
      else if (oReq == "minutes")
      {
        output = minutes;
      }
      else if (oReq == "seconds")
      {
        output = seconds;
      }
     
      return output;
    }
	
	    function trim(oString)
    {
      return oString.replace(/^([\s\t\n]|\&nbsp\;)+|([\s\t\n]|\&nbsp\;)+$/g, '');
    }

	var Studios = [];
    function findStudios(oStudios, oTemplatePath)
    {
      Studios = oStudios.split("/");
      var oFile = new ActiveXObject("Scripting.FileSystemObject");
      var path='';
      for(var i = 0; i < Studios.length; i++)
      {
        path = oTemplatePath + '\\..\\common\\studios\\' + trim(Studios[i]) + '.png';
        if (oFile.FileExists(path))
        {
          return path;
        }
      }
      return Studios[0];
    }
	
		function getRatingPercent(oRatingText)
    {
      var numbers = [];
      numbers = oRatingText.split("/");
      return Math.round(100*parseFloat(numbers[0])/parseFloat(numbers[1]));
    }

    ]]>

  </msxsl:script>

  <xsl:template match="@*|node()">
    <xsl:copy>
      <xsl:apply-templates select="@*|node()"/>
    </xsl:copy>
  </xsl:template>
 
  <xsl:template match="//Elements">
    <xsl:copy>
      <xsl:apply-templates select="@*|node()"/>
      <!-- if the value of the %disablethisS1% token is not empty-->
      <xsl:if test="//tokens/token[@name='%disablethisS1%'] != '' ">
        <ImageElement Name="disablethisPNG" X="147" Y="353" Width="18" Height="15" Source="File" Offset="0" MultiPageIndex="-1" NullImageUrl="" UseSourceDpi="False">
          <xsl:attribute name="SourceData">
            <xsl:value-of select="concat($TemplatePath, string('\..\common\images\disablethiss.png'))"/>
          </xsl:attribute>
        </ImageElement>
        <ImageElement Name="disablethis" X="600" Y="389" Width="27" Height="24" Source="File" Offset="0" MultiPageIndex="-1" NullImageUrl="" UseSourceDpi="False">
          <xsl:attribute name="SourceData">
            <xsl:value-of select="concat($TemplatePath, string('\..\common\images\disablethiss.png'))"/>
          </xsl:attribute>
        </ImageElement>
      </xsl:if>
      <xsl:if test="//tokens/token[@name='%EXTERNALdisablethisS%'] != '' ">
        <ImageElement Name="disablethisPNG" X="147" Y="353" Width="18" Height="15" Source="File" Offset="0" MultiPageIndex="-1" NullImageUrl="" UseSourceDpi="False">
          <xsl:attribute name="SourceData">
            <xsl:value-of select="concat($TemplatePath, string('\..\common\images\disablethiss.png'))"/>
           </xsl:attribute>
        </ImageElement>
        <ImageElement Name="disablethis" X="600" Y="389" Width="27" Height="24" Source="File" Offset="0" MultiPageIndex="-1" NullImageUrl="" UseSourceDpi="False">
          <xsl:attribute name="SourceData">
            <xsl:value-of select="concat($TemplatePath, string('\..\common\images\disablethiss.png'))"/>
           </xsl:attribute>
        </ImageElement>
      </xsl:if>
    </xsl:copy>
  </xsl:template>
  
  <xsl:template match="TextElement[@Name='TextAbove']/@Text">
    <xsl:variable name="hours" select="myjs:getTime($DurationSec, $Runtime,'hours')"/>
    <xsl:variable name="minutes" select="myjs:getTime($DurationSec, $Runtime,'minutes')"/>
    <xsl:variable name="seconds" select="myjs:getTime($DurationSec, $Runtime,'seconds')"/>
    <xsl:choose>
      <xsl:when test="($hours > 1)">
        <xsl:attribute name="Text">
          <xsl:value-of select="concat('HOURS','')"/>
        </xsl:attribute>
      </xsl:when>
      <xsl:when test="$hours > 0">
        <xsl:attribute name="Text">
          <xsl:value-of select="concat('HOUR','')"/>
        </xsl:attribute>
      </xsl:when>
      <xsl:when test="$minutes > 2">
        <xsl:attribute name="Text">
          <xsl:value-of select="concat('MINUTES','')"/>
        </xsl:attribute>
      </xsl:when>
      <xsl:otherwise>
        <xsl:attribute name="Text">
          <xsl:value-of select="concat('MINUTE','')"/>
        </xsl:attribute>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <xsl:template match="TextElement[@Name='HourNumber']/@Text">
    <xsl:variable name="hours" select="myjs:getTime($DurationSec, $Runtime,'hours')"/>
    <xsl:variable name="minutes" select="myjs:getTime($DurationSec, $Runtime,'minutes')"/>
    <xsl:variable name="seconds" select="myjs:getTime($DurationSec, $Runtime,'seconds')"/>
    <xsl:choose>
      <xsl:when test="$hours > 0">
        <xsl:attribute name="Text">
          <xsl:value-of select="concat(string($hours),'')"/>
        </xsl:attribute>
      </xsl:when>
      <xsl:otherwise>
        <xsl:attribute name="Text">
          <xsl:value-of select="concat(string($minutes),'')"/>
        </xsl:attribute>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <xsl:template match="TextElement[@Name='TextBelow']/@Text">
    <xsl:variable name="hours" select="myjs:getTime($DurationSec, $Runtime,'hours')"/>
    <xsl:variable name="minutes" select="myjs:getTime($DurationSec, $Runtime,'minutes')"/>
    <xsl:variable name="seconds" select="myjs:getTime($DurationSec, $Runtime,'seconds')"/>
    <xsl:choose>
	  <xsl:when test="($hours > 0) and ($minutes >= 10)">
        <xsl:attribute name="Text">
          <xsl:value-of select="concat($minutes,' ','MINUTES')"/>
        </xsl:attribute>
      </xsl:when>
      <xsl:when test="($hours > 0) and ($minutes > 1)">
        <xsl:attribute name="Text">
          <xsl:value-of select="concat('0',$minutes,' ','MINUTES')"/>
        </xsl:attribute>
      </xsl:when>
	  <xsl:when test="($hours > 0) and ($minutes = 1)">
        <xsl:attribute name="Text">
          <xsl:value-of select="concat('0',$minutes,' ','MINUTE')"/>
        </xsl:attribute>
      </xsl:when>
      <xsl:when test="($hours > 0) and ($minutes >= 0)">
        <xsl:attribute name="Text">
          <xsl:value-of select="concat($minutes,' ','MINUTE')"/>
        </xsl:attribute>
      </xsl:when>
	  <xsl:when test="($hours = 0) and ($seconds >= 10)">
        <xsl:attribute name="Text">
          <xsl:value-of select="concat($seconds,' ','SECONDS')"/>
        </xsl:attribute>
      </xsl:when>
      <xsl:when test="($hours = 0) and ($seconds > 1)">
        <xsl:attribute name="Text">
          <xsl:value-of select="concat('0',$seconds,' ','SECONDS')"/>
        </xsl:attribute>
      </xsl:when>
	  <xsl:when test="($hours = 0) and ($seconds = 1)">
        <xsl:attribute name="Text">
          <xsl:value-of select="concat('0',$seconds,' ','SECOND')"/>
        </xsl:attribute>
      </xsl:when>
      <xsl:otherwise>
        <xsl:attribute name="Text">
          <xsl:value-of select="concat($seconds,' ','SECOND')"/>
        </xsl:attribute>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <xsl:template match="TextElement[@Name='TextBelow']">
    <xsl:variable name="hours" select="myjs:getTime($DurationSec, $Runtime,'hours')"/>
    <xsl:variable name="minutes" select="myjs:getTime($DurationSec, $Runtime,'minutes')"/>
    <xsl:variable name="seconds" select="myjs:getTime($DurationSec, $Runtime,'seconds')"/>
    <xsl:copy>
      <xsl:apply-templates select="@*|node()"/>
      <xsl:choose>
        <xsl:when test="$hours >= 1">
          <xsl:choose>
            <xsl:when test="$minutes >= 10">
              <Actions>
                <Stretch Height="18" Width="81" />
              </Actions>  
            </xsl:when>
            <xsl:when test="$minutes >= 2">
              <Actions>
                <Stretch Height="18" Width="81" />
              </Actions>
            </xsl:when>
			<xsl:when test="$minutes = 1">
              <Actions>
                <Stretch Height="18" Width="89" />
              </Actions>
            </xsl:when>
            <xsl:when test="$minutes >= 0">
              <Actions>
                <Stretch Height="18" Width="102" />
              </Actions>
            </xsl:when>
          </xsl:choose>
        </xsl:when>
        <xsl:when test="$hours = 0">
          <xsl:choose>
            <xsl:when test="$seconds >= 10">
              <Actions>
                <Stretch Height="18" Width="81" />
              </Actions>
            </xsl:when>
            <xsl:when test="$seconds >= 2">
              <Actions>
                <Stretch Height="18" Width="81" />
              </Actions>
            </xsl:when>
		    <xsl:when test="$seconds = 1">
              <Actions>
                <Stretch Height="18" Width="91" />
              </Actions>
            </xsl:when>
            <xsl:when test="$seconds >= 0">
              <Actions>
                <Stretch Height="18" Width="103" />
              </Actions>
            </xsl:when>
          </xsl:choose>
        </xsl:when>
      </xsl:choose>
    </xsl:copy>
  </xsl:template>
  
  <xsl:template match="TextElement[@Name='TextAbove']">
    <xsl:variable name="hours" select="myjs:getTime($DurationSec, $Runtime,'hours')"/>
    <xsl:variable name="minutes" select="myjs:getTime($DurationSec, $Runtime,'minutes')"/>
    <xsl:variable name="seconds" select="myjs:getTime($DurationSec, $Runtime,'seconds')"/>
    <xsl:copy>
      <xsl:apply-templates select="@*|node()"/>
      <xsl:choose>
		<xsl:when test="($hours = 1)">
          <Actions>
            <Stretch Height="13" Width="74" />
          </Actions>
        </xsl:when>
		<xsl:when test="($hours > 1)">
          <Actions>
            <Stretch Height="13" Width="74" />
          </Actions>
        </xsl:when>
        <xsl:when test="($minutes > 0)">
          <Actions>
            <Stretch Height="13" Width="75" />
          </Actions>
        </xsl:when>
      </xsl:choose>
    </xsl:copy>
  </xsl:template>
  
  <xsl:template match="//ImageElement[@Name='SourceFlags']/@SourceData">
    <xsl:attribute name="SourceData">
      <xsl:value-of select="string(myjs:getSourceFlags($TemplatePath,$FilePath,$FileName))"/>
    </xsl:attribute>
  </xsl:template> 
  
  <xsl:template match="//ImageElement[@Name='disablethis1']/@SourceData">
    <xsl:variable name="Sub1" select="string(//tokens/token[@name='%disablethisS1%'])"/>
    <xsl:variable name="ExtSub1" select="string(//tokens/token[@name='%EXTERNALdisablethisS1%'])"/>
    <xsl:choose>
      <xsl:when test="$Sub1 = '' ">
        <xsl:attribute name="SourceData">
          <xsl:value-of select="concat($TemplatePath,string('\..\common\countries\'),$ExtSub1,string('.png'))"/>
        </xsl:attribute> 
      </xsl:when>
      <xsl:otherwise>
        <xsl:attribute name="SourceData">
          <xsl:value-of select="concat($TemplatePath,string('\..\common\countries\'),$Sub1,string('.png'))"/>
        </xsl:attribute>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>
  
  <xsl:template match="//ImageElement[@Name='disablethis2']/@SourceData">
    <xsl:variable name="Sub1" select="string(//tokens/token[@name='%disablethisS1%'])"/>
    <xsl:variable name="Sub2" select="string(//tokens/token[@name='%disablethisS2%'])"/>
    <xsl:variable name="ExtSub1" select="string(//tokens/token[@name='%EXTERNALdisablethisS1%'])"/>
    <xsl:variable name="ExtSub2" select="string(//tokens/token[@name='%EXTERNALdisablethisS2%'])"/>
    <xsl:choose>
      <xsl:when test="($Sub1 = '') and ($ExtSub1 != '') ">
        <xsl:attribute name="SourceData">
          <xsl:value-of select="string('')"/>
        </xsl:attribute>
      </xsl:when>
      <xsl:when test="($Sub1 != '') and ($Sub2 = '') and ($ExtSub1 != '') ">
        <xsl:attribute name="SourceData">
          <xsl:value-of select="concat($TemplatePath,string('\..\common\countries\'),$ExtSub1,string('.png'))"/>
        </xsl:attribute>
      </xsl:when>
      <xsl:otherwise>
        <xsl:attribute name="SourceData">
          <xsl:value-of select="concat($TemplatePath,string('\..\common\countries\'),$Sub2,string('.png'))"/>
        </xsl:attribute>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>
  
      <xsl:template match="//ImageElement[@Name='disablethis3']/@SourceData">
    <xsl:variable name="Sub2" select="string(//tokens/token[@name='%disablethisS2%'])"/>
    <xsl:variable name="Sub3" select="string(//tokens/token[@name='%disablethisS3%'])"/>
    <xsl:variable name="ExtSub1" select="string(//tokens/token[@name='%EXTERNALdisablethisS1%'])"/>
    <xsl:variable name="ExtSub2" select="string(//tokens/token[@name='%EXTERNALdisablethisS2%'])"/>
    <xsl:choose>
      <xsl:when test="($Sub2 = '') and ($ExtSub1 != '') ">
        <xsl:attribute name="SourceData">
          <xsl:value-of select="string('')"/>
        </xsl:attribute>
      </xsl:when>
      <xsl:when test="($Sub2 != '') and ($Sub3 = '') and ($ExtSub1 != '') ">
        <xsl:attribute name="SourceData">
          <xsl:value-of select="concat($TemplatePath,string('\..\common\countries\'),$ExtSub1,string('.png'))"/>
        </xsl:attribute>
      </xsl:when>
      <xsl:otherwise>
        <xsl:attribute name="SourceData">
          <xsl:value-of select="concat($TemplatePath,string('\..\common\countries\'),$Sub3,string('.png'))"/>
        </xsl:attribute>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <xsl:template match="//ImageElement[@Name='ExternalSub1']/@SourceData">
    <xsl:variable name="Sub1" select="string(//tokens/token[@name='%disablethisS1%'])"/>
    <xsl:variable name="Sub2" select="string(//tokens/token[@name='%disablethisS2%'])"/>
	<xsl:variable name="Sub3" select="string(//tokens/token[@name='%disablethisS3%'])"/>
    <xsl:variable name="ExtSub1" select="string(//tokens/token[@name='%EXTERNALdisablethisS1%'])"/>
    <xsl:choose>
      <xsl:when test="($Sub1 = '') or ($Sub2 = '')">
        <xsl:attribute name="SourceData">
          <xsl:value-of select="string('')"/>
        </xsl:attribute>
      </xsl:when>
	  <xsl:when test="($Sub2 = '') or ($Sub3 = '')">
        <xsl:attribute name="SourceData">
          <xsl:value-of select="string('')"/>
        </xsl:attribute>
      </xsl:when>
      <xsl:otherwise>
        <xsl:attribute name="SourceData">
          <xsl:value-of select="concat($TemplatePath,string('\..\common\countries\'),$ExtSub1,string('.png'))"/>
        </xsl:attribute>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>
  
  <xsl:template match="//ImageElement[@Name='SourceCover']/@SourceData">
    <xsl:attribute name="SourceData">
      <xsl:value-of select="string(myjs:getBoxes($TemplatePath,$FilePath,$FileName))"/>
    </xsl:attribute>
  </xsl:template>
  
    <xsl:template match="//ImageElement[@Name='Studio']/@SourceData">
    <xsl:attribute name="SourceData">
      <xsl:value-of select="string(myjs:findStudios($Studios,$TemplatePath))"/>
    </xsl:attribute>
  </xsl:template>
  
    <xsl:template match="//TextElement[@Name='StudioText']/@Text">
    <xsl:attribute name="Text">
      <xsl:if test="not(contains(string(myjs:findStudios($Studios,$TemplatePath)),'.png'))">
        <xsl:value-of select="string(myjs:findStudios($Studios,$TemplatePath))"/>
      </xsl:if>
    </xsl:attribute>
  </xsl:template>
  
  <xsl:template match="//TextElement[@Name='IMDB Rating']">
    <xsl:copy>
      <xsl:apply-templates select="@*|node()"/>
      <xsl:choose>
        <xsl:when test="myjs:getRatingPercent($RATING) >= 80">
          <xsl:attribute name="StrokeColor">-256</xsl:attribute>
          <xsl:attribute name="ForeColor">-256</xsl:attribute>
        </xsl:when>
        <xsl:when test="myjs:getRatingPercent($RATING) >= 50">
          <xsl:attribute name="StrokeColor">-16777216</xsl:attribute>
          <xsl:attribute name="ForeColor">-2500135</xsl:attribute>
        </xsl:when>
	    <xsl:when test="myjs:getRatingPercent($RATING) >= 0">
          <xsl:attribute name="StrokeColor">-1</xsl:attribute>
          <xsl:attribute name="ForeColor">-65536</xsl:attribute>
        </xsl:when>
      </xsl:choose>
    </xsl:copy>
  </xsl:template>
  
</xsl:stylesheet>
