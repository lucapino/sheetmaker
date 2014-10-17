<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:msxsl="urn:schemas-microsoft-com:xslt" xmlns:cs="urn:custom-CSharp" xmlns:myCPrefix="urn:custom-net" xmlns:myjs="urn:custom-javascript" exclude-result-prefixes="msxsl cs myjs">
  <msxsl:script language="JavaScript" implements-prefix="myjs">
  
    <![CDATA[
	
		
	/*****************************************************************************/
	/*                   STUDIO NAME AND CERTIFICATIONS                          */
    //movieName - Series name
	//movieNameS - Studio name
	//movieNameC - Certification label
	
	var movieName  = ['event','misfits','community','pushing daisies'];
    var movieNameS = ['nbc','e4','nbc',''];
	var movieNameC = ['','18','tv-pg','12']
	
    /*****************************************************************************/
		
    /*****************************************************************************/
    /* Configuration variables                                                   */
    /*****************************************************************************/
    var REDColor = -65536;
    var BLUEColor = -16728065;
    var TotalThumbsShown = 19; // Used to control Thumb rollover doubles. Set to the total of thumbnails you are displaying in 1 sheet
    var ThumbsRollOver = 0; //Set to 0 (zero) if you do not wish to have the Thumbnails rollover
    var commonPath = '\\..\\common\\';
    var movieTypes = ['bluray','web-dl','hdtv','bdrip','.m2ts','dvdscr','dvdrip','hddvd','generic'];
    var movieTypesG = ['blue','blue','blue','blue','blue','red','red','red','red'];
    var movieTypesC = [BLUEColor,BLUEColor,BLUEColor,BLUEColor,BLUEColor,REDColor,REDColor,REDColor,REDColor];
	var movieSource = ['hdtv','bluray','web-dl','dvdrip','.iso'];
    var movieResolutions = ['288','480','576','720','1080'];
    var movieResolutionsG = ['red','red','red','blue','blue'];
    var movieResolutionsC = [REDColor,REDColor,REDColor,BLUEColor,BLUEColor];
    var seriesMpaa = ['tv-14','tv-ma','tv-pg','tv-y','tv-y7','tv-y7f7','18','15','12a','12','unrated','nr'];
    var seriesMpaaC = ['Parents Strongly Cautioned','For Mature Audience Only','Parental Guidance Suggested','For All Children Ages 2-5','Directed To Older Children','Directed To Older Children - Fantasy Violence','For Adults Only','Suitable Only For Those Aged 15 And Over','Suitable Only For Those Aged 12 And Over','May Be Unsuitable For Under 12s','Unrated','Not Rated'];

	function getSourceFlags(oResolution,oFilePath,oFileName)
    {
      var Output = '';
      for (var i = 0; i < movieSource.length ; i++)
      {
        if ((oFilePath + '\\' + oFileName).toLowerCase().indexOf(movieSource[i]) != -1)
        {
          Output = movieSource[i] + '.png';
          break;
        }
      }
      if (Output == '')
      {
        Output = 'generic.png';
        for (var i = 0; i < movieResolutions.length ; i++)
        {
          if (oResolution.toLowerCase().indexOf(movieResolutions[i]) != -1)
          {
            Output = movieResolutions[i] + '.png';
            break;
          }
        }
      }
      return Output;
    }
	
	function getSeriesMPAA(oCERTIFICATION,oTITLE)
    {
      var Output = '';
      for (var i = 0; i < seriesMpaa.length ; i++)
      {
        if (oCERTIFICATION.toLowerCase().indexOf(seriesMpaa[i]) != -1)
        {
          Output = seriesMpaaC[i];
            break;
          }
        }
		if (Output == '')
       {
        Output = 'nr';
        for (var i = 0; i < movieName.length ; i++)
        {
          if (oTITLE.toLowerCase().indexOf(movieName[i]) != -1)
          {
            Output = movieNameC[i];
            break;
          }
        }
	   }
        for (var i = 0; i < seriesMpaa.length ; i++)
        {
          if (Output.toLowerCase().indexOf(seriesMpaa[i]) != -1)
          {
            Output = seriesMpaaC[i];
            break;
          }
        }
       return Output;
      }
    
    /*****************************************************************************/
    /* Variables used during functions, PLEASE DO NOT MODIFY DEFAULT VALUES      */
    /*****************************************************************************/
    var foundImages = []; //Stores the thumbnaisl found
    var EpNumbers = [];   //Stores the Episodes Numbers
    var EpNames = [];     //Stores the Episode Titles
    var myPos=0;          //stores the position of current file being processed
    var myEpNumber=0;     //stores the Episode Number of current file being processed
    var myPath=''; 
    var doubleEp=0;       //Indicate if current file being processed is a "double epidode"
    //var mySortIndex = '\/:!#$%&()' + '\;' + ' ,-@[]^{}~+=' + '\'' + '0123456789_abcdefghijklmnopqrstuvwxyz'; //Sorting Index for the WDTV Live
    var mySortIndex = '\/:!#$%&()\; ,-@[]^{}~+=\'0123456789_abcdefghijklmnopqrstuvwxyz'; //Sorting Index for the WDTV Live
    
    /*****************************************************************************/
    /* getFolderPathLevel                                                        */
    /* Returns different levels paths for the path provided                      */
    /*****************************************************************************/
    function getFolderPathLevel(oFilePath, Level)
    {
      //Level descripttion
      //example:         oFilePath  =  X:\Level1\Level2\Level3\Level4
      //0=current folder           --> X:\Level1\Level2\Level3\Level4
      //1=Parent folder            --> X:\Level1\Level2\Level3
      //2=Parent's Parent Folder   --> X:\Level1\Level2
      //3=...                      --> X:\Level1
      var oFile = new ActiveXObject("Scripting.FileSystemObject");

      var oPath = ' ';
      if (Level == 0)
      {
        oPath = oFilePath;
      }
      if (Level == 1)
      {
         oPath = oFile.GetParentFolderName(oFilePath);
      }
      if (Level == 2)
      {
        oPath = oFile.GetParentFolderName(oFile.GetParentFolderName(oFilePath));
      }
      if (Level == 3)
      {
        oPath = oFile.GetParentFolderName(oFile.GetParentFolderName(oFile.GetParentFolderName(oFilePath)));
      }
      return oPath;
    }
    
    /*****************************************************************************/
    /* alphabetical                                                              */
    /* Sorts values depending on the Sorting Index                               */
    /*****************************************************************************/
    function alphabetical(a, b){
      var A = a.toLowerCase();
      var B = b.toLowerCase();
      for (var i=0 ; i < A.length ; i++){
        if (i >= B.length)
        {
          return 1;
        }
        if (mySortIndex.indexOf(A.charAt(i)) < mySortIndex.IndexOf(B.charAt(i)))
        {
          return -1;
        }
        if (mySortIndex.indexOf(A.charAt(i)) > mySortIndex.IndexOf(B.charAt(i)))
        {
          return 1;
        }
      }
      return 0;
    }
    
    /*****************************************************************************/
    /* getImagePath                                                              */
    /* Returns the path to the file at the corresponding index                   */
    /* Will return an empty string if index is out of bounds                     */
    /*****************************************************************************/
    function getImagePath(i)
    {
      if((i >= 0) && (i < foundImages.length))
      {
        return foundImages[i];
      }
      else
      {
        return '';
      }
    }
    
    /*****************************************************************************/
    /* getImageByPos                                                             */
    /* Returns the image at the specified thumbnail position in the sheet        */
    /* Will do a Thumbnail rollover if "ThumbsRollOver" = 1                      */
    /* Will only rollover if theres at least "TotalThumbsShown" files detected   */
    /*****************************************************************************/
    function getImageByPos(i)
    {
      if ((myPos+i < foundImages.length) && (myPos+i >= 0))
      {
        return getImagePath(myPos+i);
      }
      else if ((myPos+i >= foundImages.length) && (foundImages.length >= TotalThumbsShown) && (ThumbsRollOver))
      {
        return getImagePath(myPos+i-(foundImages.length*Math.floor((myPos+i)/foundImages.length)));
      }
      else if ((myPos+i < 0) && (foundImages.length >= TotalThumbsShown) && (ThumbsRollOver))
      {
        return getImagePath(myPos+i+(foundImages.length*(1+(Math.floor((Math.abs(myPos+i+1))/foundImages.length)))));
      }
      else
      {
        return '';
      }
    }
    
    /*****************************************************************************/
    /* processSubFolders                                                         */
    /* Detect the "folder.jpg" files at the corresponding folder depth           */
    /* and stores paths into array "foundImages"                                 */
    /* then sorts that array using the WDTV sort index                           */
    /* then detect the position of the current file within the sorted array      */
    /* Trying to merge this function with "processCurrentFolder", so that same   */
    /* function can be called regardless if you are processing a subfolder or a  */
    /* current folder                                                            */
    /*****************************************************************************/
    function processSubFolders(oFilePath, folderJpgName, depth)
    {
      myPath = oFilePath;
      if(oFilePath != '')
      {
        var folderPath = getFolderPathLevel(oFilePath, depth);
        if(folderPath != '')
        {
          var oFile = new ActiveXObject("Scripting.FileSystemObject");
          var oFolder = oFile.GetFolder(folderPath);
          var oSubFolders = oFolder.SubFolders;
          var _result = '';
          if(oSubFolders.Count != 0)
          {
            for(var e= new Enumerator(oSubFolders); !e.atEnd(); e.moveNext())
            {
              if (folderPath.length == 3)
              {
                var _folderJpgPath = folderPath + e.item().Name + '\\' + folderJpgName;
              }
              else
              {
                var _folderJpgPath = folderPath + '\\' + e.item().Name + '\\' + folderJpgName;
              }
              if(oFile.FileExists(_folderJpgPath))
              {
                foundImages.push(_folderJpgPath.toLowerCase());
              }
            }
          }
          foundImages.sort(alphabetical);
          for (var i=0 ; i <= foundImages.length-1; i++)
          {
              if (foundImages[i] == (getFolderPathLevel(myPath,depth-1) + '\\' + 'folder.jpg').toLowerCase())
              {
                myPos = i;
                break;
              }
          }
          return foundImages.toString();
        }
      }
      else
      {
        return 'no reference movie found';
      }
    }
    /*****************************************************************************/
    
    //Detect the thumbnails "<moviename.jpg>" in the current folder
    //and stores paths into array "foundImages"
    //then sorts that array using the WDTV sort index
    //then detect the position of the current file being processed within the sorted array
    //This will also detect the presence of "double episodes" with a naming convention of "SSxEExEE"
    //Trying to merge this function with "processSubFolder", so that same function can be called
    //regardless if you are processing a subfolder or a current folder
    function processCurrentFolder(oFilePath, movieName, depth)
    {
      myPath = oFilePath;
      var folderPath = getFolderPathLevel(oFilePath, depth);
      if(oFilePath != '')
      {
        var oFile = new ActiveXObject("Scripting.FileSystemObject");
        var oFolder = oFile.GetFolder(folderPath);
        var oFiles = oFolder.Files;
        if (oFiles.Count != 0)
        {
          var f = new Enumerator(oFiles);
          for(var e = new Enumerator(oFiles); !e.atEnd(); e.moveNext())
          {
            var _FileNameExt = oFile.GetExtensionName(e.item().Name).toLowerCase();
            if ((_FileNameExt == "jpg") || (_FileNameExt == "png") || (_FileNameExt == "bmp"))
            {
              if (e.item().Name.toLowerCase().indexOf("_sheet") == -1)
              {
                var _FileName = oFile.GetBaseName(e.item().Name);
                var count = 0;
                for (f.moveFirst(); !f.atEnd(); f.moveNext())
                {
                  if (_FileName == oFile.GetBaseName(f.item().Name))
                  {
                    count++;
                    if (count > 1)
                    {
                      foundImages.push(myPath + '\\' + e.item().Name.toLowerCase());
                      break;
                    }
                  }
                }
              }
            }
          }
          foundImages.sort(alphabetical);
          for (var i=0 ; i <= foundImages.length-1; i++)
          {
            if (oFile.GetBaseName(foundImages[i]) == oFile.GetBaseName(movieName).toLowerCase())
            {
              myPos = i;
              if (/(.*\d\dx\d\dx\d\d - .*)/.test(movieName.toLowerCase()))
              {
                doubleEp=1;
              }
              break;
            }
          }
          return foundImages.toString();
        }        
      }
      return ' ';
    }
    /*****************************************************************************/
    
    //Returns correct Glow PNG depending on Video resolution
    //with a default value if video resolution was not found
    function getGlow(oResolution,oFilePath,oFileName)
    {
      var Output = '';
      for (var i = 0; i < movieTypes.length ; i++)
      {
        if ((oFilePath + '\\' + oFileName).toLowerCase().indexOf(movieTypes[i]) != -1)
        {
          Output = movieTypesG[i] + 'glow.png';
          break;
        }
      }
      if (Output == '')
      {
        Output = 'redglow.png';
        for (var i = 0; i < movieResolutions.length ; i++)
        {
          if (oResolution.toLowerCase().indexOf(movieResolutions[i]) != -1)
          {
            Output = movieResolutionsG[i] + 'glow.png';
            break;
          }
        }
      }
      return Output;
    }
    /*****************************************************************************/
    
    //Returns correct Border color depending on Video resolution
    //with a default value if video resolution was not found
    function getBorder(oResolution,oFilePath,oFileName)
    {
      var Output = 0;
      for (var i = 0; i < movieTypes.length ; i++)
      {
        if ((oFilePath + '\\' + oFileName).toLowerCase().indexOf(movieTypes[i]) != -1)
        {
          Output = movieTypesC[i];
          break;
        }
      }
      if (Output == '')
      {
        Output = REDColor;
        for (var i = 0; i < movieResolutions.length ; i++)
        {
          if (oResolution.toLowerCase().indexOf(movieResolutions[i]) != -1)
          {
            Output = movieResolutionsC[i];
            break;
          }
        }
      }
      return Output;
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
	
    var Language = [];
    function findLanguage(oLanguage, oTemplatePath, oPosition)
    {
      Language = oLanguage.split("/");
      var oFile = new ActiveXObject("Scripting.FileSystemObject");
      var path='';
      if(oPosition < Language.length)
      {
        path = oTemplatePath + '\\..\\common\\COUNTRIES\\' + trim(Language[oPosition]) + '.png';
        if (oFile.FileExists(path))
        {
          return path;
        }
      }
      return '';
    }
	
    function getStudio(oTemplatePath,oTITLE)
    {
      var Output = '';
      for (var i = 0; i < movieName.length ; i++)
      {
        if (oTITLE.toLowerCase().indexOf(movieName[i]) != -1)
        {
          Output = movieNameS[i] + '.png';
        }
      }
      return Output;
    } 
	
	function getCertification(oTemplatePath,oTITLE)
    {
      var Output = '';
      for (var i = 0; i < movieName.length ; i++)
      {
        if (oTITLE.toLowerCase().indexOf(movieName[i]) != -1)
        {
          Output = movieNameC[i] + '.png';
        }
      }
      return Output;
    } 
	
      // inputString = the full list with items to be processed (eg. 1,2,3,4,5,6,...30)
      // startIndex = the index(zero based) from which we want to return items
      // maxCount = how many items we want to get back
      // separator = the separator used by the inputString
      function getItems(inputString, startIndex, maxCount, separator)
        {
               // split the inputString into individual entities (based on the separator)
               var items = inputString.split(separator);
               
               // select from the items array the "interesting" part (the one between startIndex and startIndex+maxCount)
               var sliced = items.slice(startIndex,  startIndex + maxCount);
               
               // append the separator for the last item (ThumbGen is not adding it automatically so we need to fix it here)
               var result = sliced.join(separator).concat(separator);
               
               // if the result is just the separator then remove it and return an empty string
			   if (result == separator)
			   {
			      result = "";
			   }
			   
			   // return the result
               return result;
        };
	
	]]>

  </msxsl:script>
  
	<msxsl:script implements-prefix="cs" language="CSharp">
    <msxsl:using namespace="System.Text.RegularExpressions" />
    <msxsl:using namespace="System.Globalization" />
    <msxsl:using namespace="System.Drawing"/>
    <msxsl:assembly name="System.Drawing"/>
	<![CDATA[

	 public int CalculateRatingWidth(string rating, int totalWidth)
	 {
		string[] _r = rating.Split('/');
		if (_r.Length == 2)
		{
			System.Globalization.NumberFormatInfo provider = new System.Globalization.NumberFormatInfo( );
            provider.NumberDecimalSeparator = ".";
            double _rating = !string.IsNullOrEmpty(_r[0]) ? Convert.ToDouble(_r[0]) : 1; 
			return (int)(totalWidth * _rating / 10);
		}
		return 1;
		}
		
	  public int getHeight(string scrImg)
      {
        Image objImage = Image.FromFile(scrImg);
        return objImage.Height;
      }

	]]>
	
	</msxsl:script>
</xsl:stylesheet>
    
		
		