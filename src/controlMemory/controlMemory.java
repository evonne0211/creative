package controlMemory;
import java.io.*; 
import java.lang.Runtime;
import jcx.net.smtp;
import jcx.util.*; 

public class controlMemory {

	public static void main(String[] args) {
		Runtime run = Runtime.getRuntime();
		long max = run.maxMemory()/1024/1024;
		long total = run.totalMemory()/1024/1024;
		long free = run.freeMemory()/1024/1024;
		long usable = max - total + free;
		//最大可用記憶體 （usable）這是JVM真正還可以再繼續使用的記憶體（不考慮之後垃圾回收再次得到的記憶體）。由【最大記憶體 - 已分配記憶體 + 已分配記憶體中的剩餘空間】計算得到。
        //已分配記憶體（totalMemory）jvm使用的記憶體都是從本地系\u7edf獲取的，但是通常jvm\u521a啟動的時候，並不會向系\u7edf申\u8bf7全部的記憶體。而是根據所加\u8f7d的Class和相關\u8d44源的容量來\u51b3定的。 
        //已分配記憶體中的剩餘空間(freeMemory) 這是相對於分配記憶體（totalMemeory）計算的，相當於totalMemory - 已使用記憶體。當freeMemory 快要接近0時，已分配的記憶體即將耗盡，JVM會\u51b3定再次向系\u7edf申請更多的記憶體。 
		String[] usr = new String[2];
		usr[0]="00285@dinyi.com.tw";
		usr[1]="00748@dinyi.com.tw";
		String content=convert.StrToByte("最大記憶體 = " + max+" m;  <br>");
        content=content+convert.StrToByte("已分配記憶體 = " + total+" m;  <br>");
        content=content+convert.StrToByte("已分配記憶體中的剩餘空間 = " + free+" m;  <br>");
        content=content+convert.StrToByte("最大可用記憶體 = " + usable+" m <br>");
		if(usable<800){
		String sendRS = smtp.sendMailbcc("mail.dinyi.com.tw,25,emaker,emaker","info@dinyi.com.tw",usr,"ERP Server memory usable "+ usable+" m",content, null, "","text/html");
		}

	}

}
