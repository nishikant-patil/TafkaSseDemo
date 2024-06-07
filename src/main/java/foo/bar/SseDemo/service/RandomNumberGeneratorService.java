package foo.bar.SseDemo.service;

import foo.bar.SseDemo.controller.RandomNumberController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Service
public class RandomNumberGeneratorService {
    private static final Logger logger = LoggerFactory.getLogger(RandomNumberController.class);

    private final ConcurrentHashMap<SseEmitter, Integer> sseEmitterList = new ConcurrentHashMap<>();

    public void registerSseEmitter(SseEmitter sseEmitter) {
        logger.info("Registering {}", sseEmitter);
        sseEmitterList.put(sseEmitter, 1);
    }

    public void deRegisterSseEmitter(SseEmitter sseEmitter) {
        logger.info("Deregistering {}", sseEmitter);
        sseEmitterList.remove(sseEmitter);
    }

    @EventListener(ApplicationReadyEvent.class)
    private void generateNumbers() throws InterruptedException {
        var random = new Random();
        logger.info("Generating random numbers now!");
        while (true) {
            int i = random.nextInt();
            logger.info("Generated {}, sending it out to {} subscribers", i, sseEmitterList.size());
            sseEmitterList.keySet().forEach(sseEmitter -> {
                try {
                    sseEmitter.send(i);
                } catch (IOException e) {
                    logger.error("Enable to send to emitter {} due to {}.", sseEmitter, e.getMessage());
                }
            });
            TimeUnit.SECONDS.sleep(1);
        }
    }
}
