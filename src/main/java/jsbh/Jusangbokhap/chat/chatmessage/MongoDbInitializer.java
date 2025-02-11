package jsbh.Jusangbokhap.chat.chatmessage;

import org.springframework.boot.CommandLineRunner;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class MongoDbInitializer implements CommandLineRunner {

	private final MongoTemplate mongoTemplate;

	@Override
	public void run(String... args) {
		// 기존 컬렉션 삭제 후 다시 생성
		mongoTemplate.getDb().drop();
	}
}