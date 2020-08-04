package com.example.asus.mp3player.Model;

public class Function {
    public static String convertMilisecondstoTimer(long milisecond){
        String timer="";
        int hour=(int)(milisecond/1000)/3600;
        int minute=(int)((milisecond/1000)%3600)/60;
        int second=(int)((milisecond/1000)%3600)%60;

        if(hour>0)
            timer+=hour+":";
        if(second<10)
            timer+=minute+":0"+second;
        else
            timer+=minute+":"+second;
        return timer;
    }
    public static int percentageOfProgress(long currentTime, long totalTime)
    {
        Double percent= ((double)currentTime/totalTime)*100;
        return percent.intValue();
    }

    public static long progressToMilisecond(int progress,long totalTime)
    {
        return (totalTime*progress)/100;
    }
}