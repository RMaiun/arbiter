const { MessageUpdate } = require("typegram/update");

const Context = require('telegraf').Context;

const inputChannel = 'input_q';
const outputChannel = "output_q"
const open = require('amqplib').connect('amqp://rabbitmq:rabbitmq@localhost:5672/ukl');


class RabbitClient{
  async constructor() {
    this._prodChannel = await open.createChannel()
    this._consChannel = await open.createChannel()
  }

  async send(ctx){
    const data = {
      cmd: "listPlayers",
      msgId: ctx.update.update_id,
      chatId: ctx.chat.id,
      tid: ctx.update.message.from.id
    }
    await this._prodChannel.assertQueue(inputChannel)
    await this._prodChannel.sendToQueue(Buffer.from(JSON.stringify(data)))
  }

  async initConsumer(bot){
    await this._consChannel.assertQueue(outputChannel)
    await this._consChannel.consume(outputChannel, function(msg){
      if (msg !== null) {
        console.log(msg.content.toString());
        this._consChannel.ack(msg);

      }
    })
  }

}