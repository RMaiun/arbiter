'use strict'
const amqp = require('amqplib');
const inputChannel = 'input_q';
const outputChannel = "output_q"

class RabbitClient{

  async publish(data){
    const connection = await amqp.connect('amqp://rabbitmq:rabbitmq@localhost:5672/ukl')
    const prodChannel = await connection.createChannel()
    await prodChannel.assertQueue(inputChannel, { durable: false })
    return prodChannel.sendToQueue(inputChannel,Buffer.from(JSON.stringify(data)))
  }

  async initConsumer(bot){
    const connection = await amqp.connect('amqp://rabbitmq:rabbitmq@localhost:5672/ukl')
    const consChannel = await connection.createChannel()
    await consChannel.assertQueue(outputChannel,{ durable: false })
    await consChannel.consume(outputChannel, async function(msg){
      if (msg !== null) {
        console.log(msg.content.toString());
        const data = JSON.parse(msg.content);
        await bot.telegram.sendMessage(data.chatId, data.result)
        consChannel.ack(msg);
      }
    })
  }

}

module.exports = {RabbitClient}