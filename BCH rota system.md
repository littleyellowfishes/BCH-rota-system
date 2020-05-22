## BCH rota system

BCH rota system is a java base spring boot project for showing the rota to user.

## Description

This project have three roles general user, administrator and spectator. User and spectator are view only to rota, spectator have print page to print out weekly rota.

Administrator can create/delete shift and edit/create/delete user.

The weekend set to color yellow for highlight.

## Contributing

Pull requests are welcome. For major changes, please open an issue first to discuss what you would like to change.

Please make sure to update tests as appropriate.

## Support

For security purpose, we exclude our repository file. 

##### To use in your project

If you using a database it need to contain  `spring.datasource.url` ,`spring.datasource.username` and `spring.datasource.password`

if you using a email that will send to user base on behavior it need to contain `spring.mail.host`, `spring.mail.port`, `spring.mail.username`, `spring.mail.password, spring.mail.properties.mail.smtp.auth = true` and 

`spring.mail.properties.mail.smtp.starttls.enable = true`

## Authors and acknowledgment

Alex Welsh

Seunghun Lee

Xin Ye

## License

 [Apache](http://www.apache.org/licenses/)

[MIT](https://choosealicense.com/licenses/mit/)

[mysql](https://www.gnu.org/licenses/old-licenses/gpl-2.0.txt)