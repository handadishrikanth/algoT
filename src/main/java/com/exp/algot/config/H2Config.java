package com.exp.algot.config;

import java.io.IOException;
import java.net.ServerSocket;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.h2.jdbcx.JdbcDataSource;
import org.h2.tools.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;

@Configuration
@Profile("default")
class H2Config {

	private static final Logger log = LoggerFactory.getLogger(H2Config.class);

	@Bean
	public DataSource dataSource(Environment env) throws SQLException {
		// For debugging data uncomment below line to start H2 web console at url
		// http://localhost:8082 &
		// connect using the URL printed by below log with username password field blank
		Server.createWebServer().start();
		String freePort = getFreePort(9092) + "";
		// Server server = Server.createTcpServer("-tcpPort", freePort, "-tcpAllowOthers", "-ifNotExists").start();
		Server server = Server.createTcpServer("-tcpPort", freePort + "", "-tcpAllowOthers", "-ifNotExists").start();
		// Server server = Server.createTcpServer("-tcpAllowOthers").start();// picks up
		// random free port
		JdbcDataSource dataSource = new JdbcDataSource();
		// jdbc:h2:file:~/MyName/EclipseWorkspace/ProjectName/TestDataBase
		String url = "jdbc:h2:tcp://localhost:" + server.getPort() + "/./db/h2;MODE=MySQL";
		log.info("Strted H2 server {}", url);
		dataSource.setUrl(url);
		return dataSource;
	}

	private int getFreePort(int port) {
		ServerSocket socket = null;
		try {
			//socket = new ServerSocket(0);
			socket = new ServerSocket(port);
			return socket.getLocalPort();
		} catch (IOException e) {
			throw new RuntimeException("Unable to get a free port", e);
		} finally {
			try {
				socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}
