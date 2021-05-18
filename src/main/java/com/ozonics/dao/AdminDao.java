package com.ozonics.dao;

import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.imageio.ImageIO;

import org.apache.commons.codec.binary.Base64;
import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.internal.Constants;
import com.ozonics.api.EmailContent;
import com.ozonics.aws.s3.config.S3MultipartUpload;
import com.ozonics.aws.s3.serv.AmazonClient;
import com.ozonics.aws.s3.serv.awsS3Impl;
import com.ozonics.aws.s3.serv.awsS3ServiceImpl;
import com.ozonics.bean.AllBean;

import sun.misc.BASE64Decoder;
@Configuration
public class AdminDao {
	JdbcTemplate template;

	private String file_ext = "/tmp/ozo/";
	private String aws_endpointURL = "https://ozonics-s3.s3.us-east-2.amazonaws.com/";
//	private String file_ext = "/usr/ozofiles/";
	private awsS3Impl service;
	private String sender_id = "support@ozonicsassets.com";
	private String sender_password = "ozonics1010";

	public void setTemplate(JdbcTemplate template) {
		this.template = template;
	}

	int count = 0;
	int count2 = 0;

	public AllBean verifyUser(String username, String password) {
		final AllBean resultBean = new AllBean();

		System.out.println("no part");
		String query = "select username, phone_num, phone_pin, email from ozonics.login where lower(username) = '"
				+ username.toLowerCase() + "' and password = '" + password + "'";
		String query1 = "Select username, phone_num, segment, category, sub_category, phone_pin, email from ozonics.users where lower(username) = '"

				+ username.toLowerCase() + "' and password = '" + password + "'";

		System.out.println(query);
		@SuppressWarnings("unchecked")
		List<String> list = template.query(query, new RowMapper() {
			public String mapRow(ResultSet rs, int arg1) throws SQLException {
				// TODO Auto-generated method stub
				String str = rs.getString("username");
				resultBean.setUsername(str);
				resultBean.setPhone_pin(rs.getInt("phone_pin"));
				resultBean.setPhone_num(rs.getString("phone_num"));
				resultBean.setSegment("All");
				resultBean.setSub_category("All");
				resultBean.setCategory("All");
				resultBean.setEmail(rs.getString("email"));
				return str;
			}
		});
		System.out.println("list:   " + list.size());
		System.out.println(query1);
		@SuppressWarnings("unchecked")

		List<String> list2 = template.query(query1, new RowMapper() {
			public String mapRow(ResultSet rs, int arg1) throws SQLException {
				// TODO Auto-generated method stub
				String str = rs.getString("username");
				resultBean.setUsername(str);
				resultBean.setPhone_num(rs.getString("phone_num"));
				resultBean.setPhone_pin(rs.getInt("phone_pin"));
				resultBean.setSegment(rs.getString("segment"));
				resultBean.setSub_category(rs.getString("sub_category"));
				resultBean.setCategory(rs.getString("category"));
				resultBean.setEmail(rs.getString("email"));
				return str;
			}
		});

		System.out.println("count:" + count);
		if (list.size() > 0) {
			resultBean.setUser_type("admin");
		} else if (list2.size() > 0) {
			resultBean.setUser_type("user");
		}
		if (list.size() > 0 || list2.size() > 0) {
			resultBean.setCount(1);
			return resultBean;
		} else {
			resultBean.setCount(0);

			return resultBean;
		}
	}

	public int uploadFile(AllBean bean, String host) throws IOException {
		String bucket_ext = "";
		String fileb64 = bean.getImage_b64();
		String file_name = bean.getFile_name();
		String[] file_arr = fileb64.split(";");
		String type = file_arr[0];
		String b64_str = file_arr[1].split(",")[1];
		String main_category = bean.getFolder().split("_")[0];
		String category = bean.getFolder().substring(bean.getFolder().indexOf("_") + 1);

		System.out.println("name of file:" + file_name + "       type:" + type);
		String filefolder = file_ext + file_name;

		File file = new File(filefolder);
		if (file.exists())
			file.delete();
		int status = 0;
		File file2 = new File(file_ext);
		boolean dirCreated = file2.mkdir();

		try {
			if (host.contains("ozonicsasset")) {
				aws_endpointURL = aws_endpointURL + "main/";
				bucket_ext = "/main";
			} else {
				aws_endpointURL = aws_endpointURL + "test/";
				bucket_ext = "/test";
			}

			FileOutputStream fos = new FileOutputStream(file);
			fos.write(Base64.decodeBase64(b64_str));
			fos.close();
			AmazonClient amazonClient = new AmazonClient();
			amazonClient.uploadFileTos3bucket(file_name, file, bucket_ext);
			file.delete();
			status = 1;
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (status == 1) {
			String awsfolder = aws_endpointURL + file_name;
			String query = "insert into ozonics.files(product_name, logtime, file_name, live_status, file_folder, category, file_type,"
					+ " username,comments, main_category, file_id) " + "values('" + bean.getCategory() + "', '"
					+ Calendar.getInstance().getTime() + "', '" + file_name + "', true, '" + awsfolder + "', '"
					+ category + "', " + "'" + type + "', '" + bean.getUsername() + "', '"
					+ bean.getComments().replaceAll("'", "''") + "', '" + main_category
					+ "', concat('FI',nextVal('ozonics.file_id')))";
			System.out.println(query);
			try {
				template.update(query);
				status = 1;
			} catch (Exception e) {
				e.printStackTrace();
				status = 0;
			}
		} else {
			status = 0;
		}
		return status;
	}

	public String mailFile(String file_id, String emailid, String host) throws IOException {

		String bucket_ext = "";
		if (host.contains("ozonicsasset")) {
			bucket_ext = "/main";
		} else {
			bucket_ext = "/test";
		}

		String query = "Select file_name from ozonics.files where file_id ='" + file_id + "'";
		String file_name = (String) template.queryForObject(query, String.class);
		System.out.println("file_name:" + file_name);

		AmazonClient client = new AmazonClient();

		byte[] encoded = client.downloadFileFrolS3bucket(file_name, bucket_ext);
		System.out.println("File received");
		File file = new File(file_ext + file_name);
		OutputStream os = new FileOutputStream(file);
		os.write(encoded);

		EmailContent content = new EmailContent();
		AllBean bean = getFileDetails(file_name);
		String commentStr = "";
		if (bean.getComments().equals(null) || bean.getComments().equals("")) {
			commentStr = "No File Info";
		} else {
			commentStr = "File Info: <br>" + bean.getComments();
		}

		String email_message = "PFA the file:" + file_name + "<br> <br>" + commentStr;
		String subject = "Ozonics Confidential Assets – " + file_name;
		String filefolder = file_ext + file_name;

		String receiver_email = emailid;
		String file_link = filefolder;
		String mail_sent = content.emailFormatwithAttachment(sender_id, sender_password, email_message, subject,
				receiver_email, file_link, file_name);
		os.close();
		file.delete();
		return mail_sent;
	}

	@SuppressWarnings("unchecked")
	public AllBean getFileDetails(String filename) {
		String query = "Select * from ozonics.files where lower(file_name) = '" + filename.toLowerCase() + "'";
		final AllBean bean = new AllBean();
		template.query(query, new RowMapper() {
			@Override
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				bean.setComments(rs.getString("comments"));
				return null;
			}

		});
		return bean;

	}

	public BufferedImage getB64toFile(String image_b64) throws IOException {
		String[] strings = image_b64.split(",");
		BufferedImage image = null;
		String image_str = strings[1];
		BASE64Decoder decoder = new BASE64Decoder();
		byte[] image_byte = decoder.decodeBuffer(image_str);
		ByteArrayInputStream bis = new ByteArrayInputStream(image_byte);
		image = ImageIO.read(bis);
		bis.close();

		return image;
	}


	final Path root = Paths.get("/home/garima/Documents/reactfiles/abc");

	public AllBean saveMultipartFile(MultipartFile file) throws IOException {
		String file_name = file.getOriginalFilename();
		AllBean resultBean = new AllBean();
//		String type = file_name.split(".")[1];

		System.out.println("name of file:" + file_name);
		String dir = "/tmp/reactfiles";

		File check_dir = new File(dir);
		if (!check_dir.exists()) {
			check_dir.mkdir();
		}

		StringBuilder fileName = new StringBuilder();
		try {

			// first way
//			Path path =Paths.get(dir, "abc.png");
//			try {
//				OutputStream io = Files.newOutputStream(path);
//				io.write(file.getBytes());
//			}catch(Exception e) {e.printStackTrace();}
//			

			// another way
//			fileName.append(file.getOriginalFilename());
//			Files.write(path, file.getBytes());
//			

			// second way
//			String dir1 = dir + File.separator + file_name;
//			InputStream in = file.getInputStream();
//			OutputStream out = new FileOutputStream(dir1);
//			System.out.println("size:" + file.getBytes());
//			Files.copy(file.getInputStream(), this.root.resolve(file.getOriginalFilename()));

//			third way
			byte[] bytes = file.getBytes();
			String content = new String(file.getBytes());
			File file_save = new File(dir + File.separator + "" + file.getOriginalFilename());
//			BufferedOutputStream stream =new BufferedOutputStream(new FileOutputStream(new File(dir + File.separator + ""+file.getOriginalFilename())));
			try {
				BufferedWriter out = new BufferedWriter(
						new OutputStreamWriter(new FileOutputStream(file_save), StandardCharsets.UTF_8));

				out.write(content);
				out.flush();
				out.close();

				resultBean.setStatus(1);
				resultBean.setFile_name(dir + File.separator + file.getOriginalFilename());

			} catch (Exception e) {
				e.printStackTrace();
				resultBean.setStatus(0);
//				resultBean.setFile_name(dir+File.separator+file.getOriginalFilename());
			}

//			in.close();
//			out.close();
			// third way
//			String filePath = dir + file.getOriginalFilename();
//			File dest = new File(filePath);
//			file.transferTo(dest);

		} catch (Exception e) {
			e.printStackTrace();
			resultBean.setStatus(0);
		}
		System.out.println("status:" + resultBean.getStatus());
		return resultBean;
	}

	public int saveFileInDb(AllBean bean) {

		String query = "insert into ozonics.files(product_name, category, level1, level2, level3, file_name, live_status ) values"
				+ " ('" + bean.getProduct_name() + "', '" + bean.getCategory() + "', '" + bean.getLevel1() + "', '"
				+ bean.getLevel2() + "', 'null', " + "'" + bean.getFile_name() + "', true)";
		int status = 0;
		System.out.println(query);
		try {
			template.update(query);
			status = 1;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return status;
	}

	public List<AllBean> sendFilesList(final AllBean bean) {
		String query = "Select * from ozonics.files where lower(category) = '" + bean.getFolder().toLowerCase()
				+ "' order by logtime desc";
		System.out.println(query);

		@SuppressWarnings("unchecked")
		List<AllBean> list = template.query(query, new RowMapper() {
			public Object mapRow(ResultSet rs, int arg1) throws SQLException {
				AllBean tempbean = new AllBean();
				tempbean.setFile_name(rs.getString("file_name"));
				tempbean.setFolder(rs.getString("file_folder"));
				tempbean.setComments(rs.getString("comments"));
				tempbean.setFile_id(rs.getString("file_id"));
				String query1 = "Select count(*) from ozonics.message_table where message_to ='" + bean.getEmail()
						+ "' and status = 'DELIVERED' and file_id='" + tempbean.getFile_id() + "'";
				int count = (Integer) template.queryForObject(query1, Integer.class);
				tempbean.setMessage_count(count);
				return tempbean;
			}

		});

		return list;
	}

	public File copyFile(MultipartFile multipart) throws IOException {

		File serverFile = new File(
				"/home/garima/Documents/reactfiles/abc" + File.separator + multipart.getOriginalFilename());
		BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(serverFile));
		byte[] bytes = multipart.getBytes();
		stream.write(bytes);
		stream.close();

		Path root1 = Paths
				.get("/home/garima/Documents/reactfiles/abc" + File.separator + multipart.getOriginalFilename());
		System.out.println("file:" + Files.probeContentType(root1));

		String fileBean = null;
		awsS3ServiceImpl service = new awsS3ServiceImpl();


		final int UPLOAD_PART_SIZE = 10 * Constants.MB;
		int bytesRead = 0, bytesAdded = 0;
		byte[] data = new byte[10 * Constants.MB];
		ByteArrayOutputStream bufferOutputStream = new ByteArrayOutputStream();
		URL url = new URL("_remote_url_of_uploading_file_");
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setRequestMethod("GET");

		InputStream inputStream = connection.getInputStream();
		while ((bytesRead = inputStream.read(data, 0, bytesRead)) != -1) {
			bufferOutputStream.write(data, 0, bytesRead);
			if (bytesAdded < UPLOAD_PART_SIZE) {
				// continue writing to same output stream unless it's size gets more than
				// UPLOAD_PART_SIZE
				bytesAdded += bytesRead;
				continue;
			}
			AmazonS3 s3Client = AmazonS3ClientBuilder.standard().withRegion(Regions.US_EAST_1).build();

			S3MultipartUpload multipartUpload = new S3MultipartUpload("lido/ozonics/files", "abc.jpeg", s3Client);
			multipartUpload.uploadPartAsync(new ByteArrayInputStream(bufferOutputStream.toByteArray()));
			bufferOutputStream.reset(); // flush the bufferOutputStream
			bytesAdded = 0;

		}

		System.out.println("success");

		return null;
	}

	public int deleteUser(final String username) {
		final String query = "delete from ozonics.users where username = '" + username + "'";
		int status = 0;
		try {
			this.template.update(query);

			status = 1;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return status;
	}

	public int updateOTPindb(final String user_type, final String otp, final String username) {

		String query = "";
		if (user_type.equals("admin")) {
			query = "update ozonics.login set otp='" + otp + "', login_time = '" + Calendar.getInstance().getTime()
					+ "' where username = '" + username + "'";

		} else {
			query = "update ozonics.users set otp='" + otp + "', login_time = '" + Calendar.getInstance().getTime()
					+ "' where username = '" + username + "'";
		}
		final String query2 = "insert into ozonics.login_details(username, login_time) values ('" + username + "', '"
				+ Calendar.getInstance().getTime() + "')";
		int status = 0;
		try {
			System.out.println(query);
			System.out.println(query2);

			this.template.update(query);
			this.template.update(query2);

			System.out.println("OTP updated successfully");
			status = 1;
		} catch (Exception e) {
			System.out.println("OTP update failed");
			e.printStackTrace();
		}
		return status;
	}

	@SuppressWarnings("unchecked")
	public List<AllBean> searchQuery(final String searchStr) {
		String comment_str = "'%((" + searchStr.toLowerCase().replaceAll(" ", ")|(").replaceAll(",", ")|(") + "))%'";
		String str = "'%" + searchStr.toLowerCase().replaceAll(",", "%','%") + "%'";
		System.out.println(str);
		String query = "select * " + "from ozonics.files where  lower(comments) similar to " + comment_str + "  or "
				+ "lower(file_name) like any (array[" + str + "]) order by logtime desc";

		System.out.println(query);
		final JSONArray arr = new JSONArray();
		final List<AllBean> list = new ArrayList<AllBean>();
		template.query(query, new RowMapper() {
			public Object mapRow(final ResultSet rs, final int arg1) throws SQLException {
				final AllBean tempbean = new AllBean();
				tempbean.setFile_name(rs.getString("file_name"));
				tempbean.setComments(rs.getString("comments"));
				tempbean.setFile_id(rs.getString("file_id"));
				arr.put(rs.getString("file_name"));

				list.add(tempbean);
				return null;
			}
		});
//		System.out.println("File size:" + list.size());
		return list;
	}

	int count1 = 0;

	private String subject;

	private String receiver_email;

	@SuppressWarnings("unchecked")
	public int verifyOTP(final String user_type, final int otp, final String username) {
		String query = "";
		if (user_type.equals("admin")) {
			query = "select count(*)  from ozonics.login where lower(username) = '" + username.toLowerCase()
					+ "' and otp = '" + otp + "' ";
		} else {
			query = "select count(*)    from ozonics.users where lower(username) = '" + username.toLowerCase()
					+ "' and otp = '" + otp + "' ";
		}
		template.query(query, new RowMapper() {

			public Integer mapRow(final ResultSet rs, final int arg1) throws SQLException {
				count1 = rs.getInt("count");
				return count1;
			}
		});
		System.out.println("length of list:" + this.count1);
		return this.count1;
	}

	public List<AllBean> getAllUsers() {
		final String query = "Select * from ozonics.users ";
		@SuppressWarnings("unchecked")
		final List<AllBean> list = template.query(query, new RowMapper() {

			public AllBean mapRow(final ResultSet rs, final int arg1) throws SQLException {
				final AllBean bean = new AllBean();

				bean.setUsername(rs.getString("username"));
				bean.setPhone_num(rs.getString("phone_num"));
				bean.setLogin_time(rs.getString("login_time"));
				bean.setPassword(rs.getString("password"));
				bean.setSegment(rs.getString("segment"));
				bean.setCategory(rs.getString("category"));
				bean.setSub_category(rs.getString("sub_category"));
				bean.setEmail(rs.getString("email"));
				return bean;
			}
		});

		return list;
	}

	@SuppressWarnings("unchecked")
	public int addUser(final AllBean bean) {
		final String check_user = "Select count(*) from ozonics.users where lower(username) = '"
				+ bean.getUsername().toLowerCase() + "'";
		if (bean.getSegment().equals("")) {
			bean.setSegment("All");
		}
		if (bean.getCategory().equals("")) {
			bean.setCategory("All");
		}
		if (bean.getSub_category().equals("")) {
			bean.setSub_category("All");
		}
		final AllBean bean2 = new AllBean();
		template.query(check_user, new RowMapper() {
			public Object mapRow(final ResultSet rs, final int arg1) throws SQLException {
				bean2.setCount(Integer.valueOf(rs.getInt("count")));
				return null;
			}

		});
		System.out.println("count of user:" + bean2.getCount());
		int status = 0;
		if (bean2.getCount() > 0) {
			status = 2;
		} else {
			final String query = "insert into ozonics.users(username, password, phone_num, segment, "
					+ "category, sub_category, login_time,phone_pin, email) values" + " ('" + bean.getUsername()
					+ "', '" + bean.getPassword() + "', " + "" + "'" + bean.getPhone_num() + "', '" + bean.getSegment()
					+ "', " + "'" + bean.getCategory() + "', '" + bean.getSub_category() + "', " + "'"
					+ Calendar.getInstance().getTime() + "', 1, '" + bean.getEmail() + "')";
			System.out.println(query);
			try {
				this.template.update(query);
				status = 1;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		System.out.println("status:" + status);
		return status;
	}

	public int editUser(final AllBean bean) {
		final String query = "update ozonics.users set password = '" + bean.getPassword() + "', " + "phone_num = '"
				+ bean.getPhone_num() + "', segment='" + bean.getSegment() + "', " + "category = '" + bean.getCategory()
				+ "', " + "sub_category='" + bean.getSub_category() + "', phone_pin = 1, email='" + bean.getEmail()
				+ "' where username = '" + bean.getUsername() + "' ";
		System.out.println(query);
		int status = 0;
		try {
			this.template.update(query);
			status = 1;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return status;
	}

	public List<AllBean> showLoginInfo() {
		final String query = "Select * from ozonics.login_details order by login_time desc";
		@SuppressWarnings("unchecked")
		final List<AllBean> list = template.query(query, new RowMapper() {

			public Object mapRow(final ResultSet rs, final int rowNum) throws SQLException {
				final AllBean bean = new AllBean();
				bean.setUsername(rs.getString("username"));
				final String datestr = rs.getString("login_time");
				final String[] date_arr = datestr.split(" ");
				final String[] date_part = date_arr[0].split("-");
				final String parsedDate = String.valueOf(date_part[1]) + "-" + date_part[2] + "-" + date_part[0] + " "
						+ date_arr[1];
				bean.setLogin_time(parsedDate);
				return bean;
			}
		});

		System.out.println("list size:" + list.size());
		return list;
	}

	public AllBean sendFileDetails(String file_id) {

		String query = "Select * from ozonics.files where file_id = '" + file_id + "'";
		final AllBean bean = new AllBean();
		template.query(query, new RowMapper<Object>() {
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				// TODO Auto-generated method stub
				bean.setFile_type(rs.getString("file_type"));
				bean.setFolder(file_ext);
				bean.setFile_name(rs.getString("file_name"));
				return null;
			}
		});

		return bean;
	}

	public List<AllBean> fileInfo() {
		String query = "Select * from ozonics.files where username is not null";
		List<AllBean> list = template.query(query, new RowMapper<AllBean>() {

			public AllBean mapRow(ResultSet rs, int rowNum) throws SQLException {
				// TODO Auto-generated method stub
				AllBean bean = new AllBean();
				bean.setUsername(rs.getString("username"));
//					bean.setLogin_time(rs.getString("logtime"));
				bean.setCategory(rs.getString("category"));
				bean.setFile_name(rs.getString("file_name"));
				bean.setComments(rs.getString("comments"));
				final String datestr = rs.getString("logtime");
				final String[] date_arr = datestr.split(" ");
				final String[] date_part = date_arr[0].split("-");
				final String parsedDate = String.valueOf(date_part[1]) + "-" + date_part[2] + "-" + date_part[0] + " "
						+ date_arr[1];
				bean.setLogin_time(parsedDate);
				return bean;
			}

		});
		return list;
	}

	public int deleteFile(String file_id, String host) {
		String bucket_ext = "";

		if (host.contains("ozonicsasset")) {
			bucket_ext = "/main";
		} else {
			bucket_ext = "/test";
		}
		AmazonClient client = new AmazonClient();
		String query1 = "Select file_name from ozonics.files where file_id ='" + file_id + "'";

		String file_name = (String) template.queryForObject(query1, String.class);
		System.out.println("file_name:" + file_name);
		int status = client.deleteFileFromS3bucket(file_name, bucket_ext);
		int result = 0;
		if (status == 1) {
			String query = "delete from ozonics.files where file_id = '" + file_id + "'";
			try {
				template.update(query);
				result = 1;
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			result = 0;
		}

		return result;
	}

	public int addComments(AllBean bean) {
		String query = "update ozonics.files set comments = '" + bean.getComments().replaceAll("'", "''")
				+ "' where lower(category) = '" + bean.getCategory().toLowerCase() + "' and file_name = '"
				+ bean.getFile_name() + "'";

		int status = 0;
		try {
			template.update(query);
			status = 1;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return status;

	}

	public int sendMessage(AllBean bean) {
		String message_to = bean.getMessage_to();
		int status = 0;
		String[] to_arr = message_to.split(",");

		for (int i = 0; i < to_arr.length; i++) {
			EmailContent content = new EmailContent();
			String filename = getFileNameById(bean.getFile_id());
			String email_message = "Hello, <br><br> Please check the following message from " + bean.getMessage_by()
					+ " <br><br><b> " + bean.getMessage()+"</b><br><br>Regards,<br>OzonicsAssets";
			String subject = "New message for:"+filename;
			String file_name = "file_id";
			String mail_sent = content.emailFormat(sender_id, sender_password, email_message, subject, to_arr[i],
					file_name);
			System.out.println("mail:" + mail_sent);
			if (mail_sent.equals("1")) {
				System.out.println("Email sent successfully");
				String query = "insert into ozonics.message_table (logtime, m_id, file_id, message_by, message_to, message,  status) values "
						+ "('" + Calendar.getInstance().getTime() + "', concat('MI',nextval('ozonics.msg_id')), " + "'"
						+ bean.getFile_id() + "', '" + bean.getMessage_by() + "', " + "'" + to_arr[i] + "', '"
						+ bean.getMessage() + "', 'DELIVERED')";
				System.out.println(query);
				try {

					template.update(query);
					status = 1;
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return status;
	}

	public List<AllBean> showMessageByFile(String file_id) {

		String query = "Select a.* from ozonics.message_table a   where file_id = '" + file_id
				+ "' order by logtime desc";

		List<AllBean> list = template.query(query, new RowMapper<AllBean>() {
			public AllBean mapRow(ResultSet rs, int rowNum) throws SQLException {
				// TODO Auto-generated method stub
				AllBean bean = new AllBean();
				bean.setFile_id(rs.getString("file_id"));
				bean.setMessage(rs.getString("message"));
				bean.setMessage_to(rs.getString("message_to"));
				bean.setMessage_by(rs.getString("message_by"));
				bean.setLogtime(rs.getString("logtime"));
				bean.setMsg_status(rs.getString("status"));
				bean.setStatus(1);
				return bean;
			}

		});
		return list;
	}

	public String getFileNameById(String file_id) {

		String query = "Select file_name from ozonics.files where file_id = '" + file_id + "'";
		String file_name = (String) template.queryForObject(query, String.class);
		System.out.println("File_name:" + file_name);
		return file_name;
	}

	public int changeMessageStatus(String file_id, String email) {
		String query = "update ozonics.message_table set status ='READ' where file_id = '" + file_id
				+ "' and message_to ='" + email + "'";
		int status = 0;
		try {
			template.update(query);
			status = 1;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return status;
	}

}
