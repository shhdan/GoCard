import java.sql.*;
import java.sql.ResultSet;
import java.util.Date;
import java.text.SimpleDateFormat;

public class Test {
	public static void main(String[] args){
		SimpleDateFormat day_format = new SimpleDateFormat("E");
		SimpleDateFormat date_format = new SimpleDateFormat("yyyyMM");
		SimpleDateFormat time_format = new SimpleDateFormat("HHmmss");
		Date now=new Date();
		ResultSet rst= null;
		System.out.println(rst);
		
		System.out.println(day_format.format(now));
		System.out.println(date_format.format(now));
		System.out.println(time_format.format(now));
		//for(int i=1;i<1000000;i++)
		{now=new Date();
		System.out.println(time_format.format(now));}
		Connection con = null;// 创建一个数据库连接
	    PreparedStatement pre = null;// 创建预编译语句对象，一般都是用这个而不用Statement
	    ResultSet result = null;// 创建一个结果集对象
	    String url="jdbc:oracle:thin:@localhost:1521:orcl";
	    String username="system"; //Oracle数据库用户名
	    String password="Theway2oracle"; //Oracle数据库密码
	    try { Class.forName("oracle.jdbc.OracleDriver"); //2.获得数据库连接
	    
	    con=DriverManager.getConnection(url,username,password); 
	    //判断数据库连接是否成功 
	    Statement stmt=con.createStatement();
	    String sql="select * from test_translink_data where bus = '45345345252'";//students是Oracle数据库中的表名
	    ResultSet rs=stmt.executeQuery(sql);
	    System.out.println("resutl: "+rs);
	    while(rs.next()) { 
	    	System.out.println(rs.getString("time")+" "+rs.getInt("bus"));
	    	}

	    }
	    catch (Exception ex) { ex.printStackTrace(); }
	    if(con!=null) { System.out.println("Oracle数据库间接成功"); }
	    else{ System.out.println("Oracle数据库连接失败"); }
	    


	}
	

}
