package foo.bar.SseDemo.controller;

import foo.bar.SseDemo.service.RandomNumberGeneratorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
public class RandomNumberController {
    private static final Logger logger = LoggerFactory.getLogger(RandomNumberController.class);
    private final RandomNumberGeneratorService randomNumberGeneratorService;

    public RandomNumberController(RandomNumberGeneratorService randomNumberGeneratorService) {
        this.randomNumberGeneratorService = randomNumberGeneratorService;
    }

    @CrossOrigin
    @GetMapping(path = "/random-numbers", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter streamRandomNumbers() {
        logger.info("New client connected, registering with the random number generated :D");
        var sseEmitter = new SseEmitter();
        randomNumberGeneratorService.registerSseEmitter(sseEmitter);
        sseEmitter.onCompletion(() -> randomNumberGeneratorService.deRegisterSseEmitter(sseEmitter));
        sseEmitter.onError((e) -> randomNumberGeneratorService.deRegisterSseEmitter(sseEmitter));
        sseEmitter.onTimeout(() -> randomNumberGeneratorService.deRegisterSseEmitter(sseEmitter));
        return sseEmitter;
    }
}
