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
		Connection con = null;// ����һ�����ݿ�����
	    PreparedStatement pre = null;// ����Ԥ����������һ�㶼�������������Statement
	    ResultSet result = null;// ����һ�����������
	    String url="jdbc:oracle:thin:@localhost:1521:orcl";
	    String username="system"; //Oracle���ݿ��û���
	    String password="Theway2oracle"; //Oracle���ݿ�����
	    try { Class.forName("oracle.jdbc.OracleDriver"); //2.������ݿ�����
	    
	    con=DriverManager.getConnection(url,username,password); 
	    //�ж����ݿ������Ƿ�ɹ� 
	    Statement stmt=con.createStatement();
	    String sql="select * from test_translink_data where bus = '45345345252'";//students��Oracle���ݿ��еı���
	    ResultSet rs=stmt.executeQuery(sql);
	    System.out.println("resutl: "+rs);
	    while(rs.next()) { 
	    	System.out.println(rs.getString("time")+" "+rs.getInt("bus"));
	    	}

	    }
	    catch (Exception ex) { ex.printStackTrace(); }
	    if(con!=null) { System.out.println("Oracle���ݿ��ӳɹ�"); }
	    else{ System.out.println("Oracle���ݿ�����ʧ��"); }
	    


	}
	

}
