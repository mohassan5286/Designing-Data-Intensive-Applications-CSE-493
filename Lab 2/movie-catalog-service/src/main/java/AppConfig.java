//import io.grpc.examples.helloworld.Top10MoviesReply.Top10MoviesServiceGrpc;
//import net.devh.boot.grpc.client.channelfactory.GrpcChannelFactory;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//@Configuration
//public class AppConfig  {
//    @Bean
//    Top10MoviesServiceGrpc.Top10MoviesServiceBlockingStub stub(GrpcChannelFactory channels) {
//        return Top10MoviesServiceGrpc.newBlockingStub(channels.createChannel("0.0.0.0:9090"));
//    }
//}