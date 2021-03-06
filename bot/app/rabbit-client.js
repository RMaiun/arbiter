'use strict'
const amqp = require('amqplib')
const inputChannel = 'input_q'
const outputChannel = 'output_q'

class RabbitClient {
  async _getConnection () {
    if (!this._conn) {
      console.log(`Asking host ${process.env.RABBITMQ_URI} for connection`)
      this._conn = await amqp.connect(process.env.RABBITMQ_URI)
    }
    return this._conn
  }

  async _getProdChannel () {
    if (!this._prodChannel) {
      const conn = await this._getConnection()
      this._prodChannel = await conn.createChannel()
    }
    return this._prodChannel
  }

  async publish (data) {
    const prodChannel = await this._getProdChannel()
    await prodChannel.assertQueue(inputChannel, { durable: false })
    return prodChannel.sendToQueue(inputChannel, Buffer.from(JSON.stringify(data)))
  }

  async initConsumer (bot) {
    const connection = await this._getConnection()
    const consChannel = await connection.createChannel()
    await consChannel.assertQueue(outputChannel, { durable: false })
    await consChannel.consume(outputChannel, async function (msg) {
      if (msg !== null) {
        const data = JSON.parse(msg.content)
        try {
          await bot.telegram.sendMessage(data.chatId, data.result,
            {
              parse_mode: 'Markdown',
              reply_markup: JSON.stringify({
                keyboard: [
                  [{ text: 'Cтатистика \uD83D\uDCC8' }, { text: 'Всі гравці \uD83D\uDDFF' }],
                  [{ text: 'Останні партії \uD83D\uDCCB' }, { text: 'Завантажити .xlsx \uD83D\uDCBE' }]
                ],
                resize_keyboard: true
              })
            })
          consChannel.ack(msg)
        } catch (e) {
          consChannel.nack(msg)
        }
      }
    })
  }
}

module.exports = { RabbitClient }
