package com.example.uriel.car_loading.get_car_info;

//json映射成的第二个类
public class Result
{

	String lsprefix;//车牌前缀
	String lsnum;//车牌剩余内容
	String carorg;//所在地识别码
	String usercarid;
	Alist list[];
	public String getLsprefix()
	{
		return lsprefix;
	}
	public void setLsprefix(String lsprefix)
	{
		this.lsprefix = lsprefix;
	}
	public String getLsnum()
	{
		return lsnum;
	}
	public void setLsnum(String lsnum)
	{
		this.lsnum = lsnum;
	}
	public String getCarorg()
	{
		return carorg;
	}
	public void setCarorg(String carorg)
	{
		this.carorg = carorg;
	}
	public String getUsercarid()
	{
		return usercarid;
	}
	public void setUsercarid(String usercarid)
	{
		this.usercarid = usercarid;
	}
	public Alist[] getList()
	{
		return list;
	}
	public void setList(Alist[] list)
	{
		this.list = list;
	}
	
}
