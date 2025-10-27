
import { Module } from '@nestjs/common';
import { WorkRepository } from './work.repository';

@Module({
  providers: [WorkRepository],
  exports: [WorkRepository],
})
export class OutModule {}
