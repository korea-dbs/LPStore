import { LoggerMiddleware } from './middleware/logger.middleware';
import { InModule } from './modules/in/in.module';

import { MiddlewareConsumer, Module, NestModule } from '@nestjs/common';

@Module({
  imports: [InModule],
})
export class AppModule implements NestModule {
  configure(consumer: MiddlewareConsumer) {
    consumer.apply(LoggerMiddleware).forRoutes('*');
  }
}
