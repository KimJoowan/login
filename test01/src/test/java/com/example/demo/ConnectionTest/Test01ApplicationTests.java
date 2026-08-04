package com.example.demo.ConnectionTest;

import static org.assertj.core.api.Assertions.assertThat;

import java.sql.Connection;
import javax.sql.DataSource;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class Test01ApplicationTests {

    @Autowired
    private DataSource dataSource;

    @Autowired
    private SqlSessionFactory sqlSessionFactory;

    @Test
    void contextLoads() {
        // 기본 스프링 부트 컨텍스트 로드 검증
    }

    @Test
    void Database_Connection_Test() throws Exception {
        // 1. DataSource를 통해 PostgreSQL 서버와 연결이 잘 되는지 확인합니다.
        try (Connection conn = dataSource.getConnection()) {
            System.out.println("=== DB 연결 성공 ===");
            System.out.println("드라이버 이름: " + conn.getMetaData().getDriverName());
            System.out.println("URL: " + conn.getMetaData().getURL());
            assertThat(conn).isNotNull();
        }
    }

    @Test
    void MyBatis_Connection_Test() {
        // 2. MyBatis의 SqlSessionFactory가 정상적으로 빈으로 등록되고 세션을 여는지 확인합니다.
        try (SqlSession session = sqlSessionFactory.openSession()) {
            System.out.println("=== MyBatis 세션 생성 성공 ===");
            assertThat(session).isNotNull();
        } catch (Exception e) {
            System.out.println("MyBatis 연결 중 오류 발생: " + e.getMessage());
            throw e;
        }
    }
    
    
}
