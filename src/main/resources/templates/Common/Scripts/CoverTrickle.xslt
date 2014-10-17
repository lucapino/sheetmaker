<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:msxsl="urn:schemas-microsoft-com:xslt" xmlns:myjs="urn:custom-javascript" exclude-result-prefixes="msxsl myjs">
  <xsl:variable name="COMMONPATH" select="string('\..\common\')"/>
  <msxsl:script language="JavaScript" implements-prefix="myjs">
  <![CDATA[
    /*****************************************************************************/
    /* Configuration variables                                                   */
    /*****************************************************************************/
    var REDColor = -65536;
    var BLUEColor = -16728065;
    var GRAYColor = -9868951;
    var TotalThumbsShown = 19; // Used to control Thumb rollover doubles. Set to the total of thumbnails you are displaying in 1 sheet
    var ThumbsRollOver = 1; //Set to 0 (zero) if you do not wish to have the Thumbnails rollover
    var commonPath = '\\..\\common\\';
    var movieTypes = ['bluray','brrip','dth','dvdrip','dvdscr','hddvd','dvd','hdtv','r5','generic','tv'];
    var movieTypesG = ['blue','blue','gray','gray','gray','red','gray','gray','red','gray','gray'];
    var movieTypesC = [BLUEColor,BLUEColor,GRAYColor,GRAYColor,GRAYColor,REDColor,GRAYColor,GRAYColor,REDColor,GRAYColor,GRAYColor];
    var movieResolutions = ['288','480','576','720','1080'];
    var movieResolutionsG = ['gray','gray','gray','blue','blue'];
    var movieResolutionsC = [GRAYColor,GRAYColor,GRAYColor,BLUEColor,BLUEColor];
    
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
    /* Trim leading and trailing white spaces from a string                      */
    /*****************************************************************************/
    function trim(oString)
    {
      return oString.replace(/^([\s\t\n]|\&nbsp\;)+|([\s\t\n]|\&nbsp\;)+$/g, '');
    }
    
    
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
    
    //Will separate Episodes Numbers and Titles so that each Number or Title can be 
    //treated independently
    //Will also add a leading zero in front of single digits numbers
    function processEpisodes(i, names, number)
    {
      myEpNumber = number;
      
      EpNumbers = i.split("\r\n");
      
      for (var j=0; j < EpNumbers.length; j++)
      {
        if (EpNumbers[j].length <= 1) 
        { 
          EpNumbers[j] = '0' + EpNumbers[j];
        }
      }
        
      EpNames = names.split("\r\n");
      
      return EpNumbers.length + ' ' + EpNames.length;
    }
    /*****************************************************************************/
    
    //Returns a string that contains the Episode Number and Title
    //example of returned string:  "01. Pilot"
    //Also does some template specific calculation to have the Titles
    //centered on a 27 "line" space, when total # of episodes is less than 27
    function getEpLn(pos)
    {
      if (EpNumbers.length >= 27)
      {
        line = EpNumbers[pos-1] + '. ' + EpNames[pos - 1] + ' ';
      }
      else
      {
        var line = '';
        var i = (27 - EpNumbers.length)%2;
        i = (27 - i - EpNumbers.length)/2;
        if ((pos-1 - i >= 0) && (pos-1 - i <= EpNumbers.length-1))
        {
          line = EpNumbers[pos-1-i] + '. ' + EpNames[pos-1 - i] + ' ';
        }
      }
      return line;
    }
    /*****************************************************************************/
    
    //Will change the color of a line when it corresponds to the current Episode #
    //this also takes in account "double episodes"
    function getColor(pos)
    {
      var color = "-1";
      if (EpNumbers.length >= 27)
      {
        if (pos == myEpNumber)
        {
          color = "-16777216";
        }
      }
      else
      {
        var line = '';
        var i = (27 - EpNumbers.length)%2;
        i = (27 - i - EpNumbers.length)/2;
        if ((pos-i == myEpNumber) || ((doubleEp == 1) && (pos-i-1 == myEpNumber)))
        {
          color = "-16777216";
        }
      }
      return color;
    }
    /*****************************************************************************/
    
    //Returns correct Box PNG depending on Video resolution
    //with a default value if video resolution was not found
    function getBoxes(oResolution,oFileName)
    {
      var Output = '';
      for (var i = 0; i < movieTypes.length ; i++)
      {
        if (oFileName.toLowerCase().indexOf(movieTypes[i]) != -1)
        {
          Output = movieTypes[i] + '.png';
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
    /*****************************************************************************/
    
    //Returns correct Glow PNG depending on Video resolution
    //with a default value if video resolution was not found
    function getGlow(oResolution,oFileName)
    {
      var Output = '';
      for (var i = 0; i < movieTypes.length ; i++)
      {
        if (oFileName.toLowerCase().indexOf(movieTypes[i]) != -1)
        {
          Output = movieTypesG[i] + 'glow.png';
          break;
        }
      }
      if (Output == '')
      {
        Output = 'grayglow.png';
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
    function getBorder(oResolution,oFileName)
    {
      var Output = 0;
      for (var i = 0; i < movieTypes.length ; i++)
      {
        if (oFileName.toLowerCase().indexOf(movieTypes[i]) != -1)
        {
          Output = movieTypesC[i];
          break;
        }
      }
      if (Output == '')
      {
        Output = GRAYColor;
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
    
    function getRatingPercent(oRatingText)
    {
      var numbers = [];
      numbers = oRatingText.split("/");
      return Math.round(100*parseFloat(numbers[0])/parseFloat(numbers[1]));
    }

    ]]>
  </msxsl:script>
</xsl:stylesheet>
  