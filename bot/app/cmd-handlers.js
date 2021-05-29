const axios = require('axios').default;
const Context = require("telegraf").Context
const { Update } = require('typegram')

const startData = `
  /start - інфа про можливості бота
  --------------------------------------
  /self - Дані про себе
  --------------------------------------
  'Гравці' - Всі існуючі юзери з id
  --------------------------------------
  'Останні матчі' [s][n] - показати n останніх матчів
  (s - опціонально, по дефолту current season)
  (n - опціонально, по дефолту 6)
  --------------------------------------
  'Статистика' [x] - рейтинг гравців у сезоні
  (x - формат сезона, опціонально)
  якщо x відсутній, то now()
  приклад: S1|2019, S4|2020
  --------------------------------------
  'Репорт' [s] - згенерувати xlsx репорт для сезону
  (s - опціонально, по дефолту - той, що на даний момент відкритий)
  --------------------------------------
  /subscribe - увімкнути сповіщення
  --------------------------------------
  /unsubscribe - вимкнути сповіщення
`
class CmdHandlers{

  startCmdHandler(ctx){
    ctx.reply(startData)
  }

  selfCmdHandler(ctx){
    return ctx.reply(`
    Особисті дані:
    messageId = ${ctx.update.update_id}
    chatId = ${ctx.chat.id}
    userId = ${ctx.update.message.from.id}
    firstName = ${ctx.update.message.from.first_name ? ctx.update.message.from.first_name : 'n/a'}
    lastName = ${ctx.update.message.from.last_name ? ctx.update.message.from.last_name : 'n/a'}
    `);
  }

  statsCmdHandler(ctx){
    console.log(ctx);

  }


  async xlsxReportCmdHandler(ctx){
    const x = await this.loadFile()
    ctx.replyWithDocument({source: x, filename: "test.xlsx"})
  }

  async loadFile(){
    try {
      const response = await axios({
        url: 'http://localhost:8080/reports/xlsx/S2|2021',
        method: 'GET',
        responseType: 'stream'
      });
      console.log(response);
      return response.data;
    } catch (error) {
      console.error(error);
      throw  error;
    }
  }

}

module.exports = new CmdHandlers();