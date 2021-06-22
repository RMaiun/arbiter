'use strict'
const amqp = require('amqplib');
const inputChannel = 'input_q';
const outputChannel = "output_q"

class RabbitConnect {
  constructor() {
    this._uri = process.env.RABBIT_URI || 'amqp://rabbitmq:rabbitmq@localhost:5672/ukl';
  }
  async connect() {
    this._connection = await amqp.connect(this._uri);
    this._chanelProd = await this._connection.createChannel();
    this._chanelCons = await this._connection.createChannel();
  }

  async disconnect() {
    await this._chanelProd.close();
    await this._chanelCons.close();
    return this._connection.close();
  }

  prodChannel() {
    return this._chanelProd;
  }

  consChannel() {
    return this._chanelCons;
  }
}

module.exports = {RabbitConnect}