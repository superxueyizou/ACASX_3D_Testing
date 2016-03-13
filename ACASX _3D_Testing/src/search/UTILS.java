package search;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.List;


public class UTILS {
	
		
	public static String readLastLine(File file, String charset) throws IOException 
	{
	  if (!file.exists() || file.isDirectory() || !file.canRead()) 
	  {
	    return null;
	  }
	  RandomAccessFile raf = null;
	  try 
	  {
	    raf = new RandomAccessFile(file, "r");
	    long len = raf.length();
	    if (len == 0L) 
	    {
	      return "";
	    }
	    else 
	    {
	      long pos = len - 1;
	      while (pos > 0) 
	      {
	        pos--;
	        raf.seek(pos);
	        if (raf.readByte() == '\n') 
	        {
	          break;
	        }
	      }
	      if (pos == 0) 
	      {
	        raf.seek(0);
	      }
	      byte[] bytes = new byte[(int) (len - pos)];
	      raf.read(bytes);
	      if (charset == null) 
	      {
	        return new String(bytes);
	      } 
	      else 
	      {
	        return new String(bytes, charset);
	      }
	    }
	  } 
	  catch (FileNotFoundException e) 
	  {
		  e.printStackTrace();
	  } 
	  finally 
	  {
	    if (raf != null)
	    {
	      try 
	      {
	        raf.close();
	      } 
	      catch (Exception e2)
	      {
	    	  e2.printStackTrace();
	      }
	    }
	  }
	  return null;
	}
	 
    

	 /**
     * 把数据集按一定的格式写到csv文件中
     * @param fileName  csv文件完整路径
     * @param title     csv文件抬头行
     * @param dataSet   数据集合
     */
    public static void writeDataSet2CSV(String fileName, String title, List<String> dataSet, boolean isAppending) 
    {
        FileWriter fw = null;
        try 
        {
            fw = new FileWriter(fileName,isAppending);
            //输出标题头
            //注意列之间用","间隔,写完一行需要回车换行"rn"
            if(!isAppending && (title!=null))
            {
            	fw.write(title);
            }            
          
            
            String content = null;
            for(int i=0;i<dataSet.size();i++) 
            {
                String dateItem = dataSet.get(i);
                //注意列之间用","间隔,写完一行需要回车换行"\n"
                content =dateItem+"\n";
                fw.write(content);
            }
           
           
        }
        catch(Exception e) 
        {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        finally 
        {
            try 
            {
	                if(fw!=null)
	                {
	                    fw.close();
	                }
            } 
            catch (IOException e) 
            {
                e.printStackTrace();
            }
        }
    }
    
    
    /**
     * 把数据条目按一定的格式写到csv文件中
     * @param fileName  csv文件完整路径
     * @param dataItem  数据条目
     */
    public static void writeDataItem2CSV(String fileName, String dataItem, boolean isAppending) 
    {
        FileWriter fw = null;
        try 
        {
            fw = new FileWriter(fileName,isAppending);
            
            //注意列之间用","间隔,写完一行需要回车换行"\n"
            String content =dataItem+"\n";
            fw.write(content);
        
        }
        catch(Exception e) 
        {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        finally 
        {
            try 
            {
	                if(fw!=null)
	                {
	                    fw.close();
	                }
            } 
            catch (IOException e) 
            {
                e.printStackTrace();
            }
        }
    }
    
}
