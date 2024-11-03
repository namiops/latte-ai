# Templates

In order to send a notification, it's necessary to prepare a template for that message and upload it to the designated Github repository.

## Upload a custom template

To create a new template send a PR to the [registry repo](https://github.com/wp-wcm/notification-template-registry) (you may need to ask **@agora-services** for access first) and add a file as shown below

```title="bill.txt template for email provider"

Subject:{{.firstName}}, your bill is ready
MIME-version: 1.0;
Content-Type: text/html;

<html>
  <body>
    <h1>Your total bill is {{.total}}¥</h1>
    <img src="https://quickchart.io/chart?width=500&height=300&chart={type:'bar',data:{labels:['January','February', 'March','April', 'May'], datasets:[{label:'Water',data:[50,60,70,180,190]},{label:'Electricity',data:[100,200,300,400,500]}]}}" />
  </body>
</html>
```

Official documentation about the templating system is available at [text/template](https://pkg.go.dev/text/template)

#### Reserved words

There is a caveat to consider while preparing a template: some of the fields might be reserved because coming from other internal providers and therefore no matter what data are added in the map used to render the template, these will always be overwritten. 

Currently, the following fields are retrieved from the Identity Provider internal user data: `firstName`,`lastName` and `email`. At the same time this makes easier to populate a template such as

```
Hello {{.firstName}}!
```
because this info is already in the system and doesn't require further handling.
