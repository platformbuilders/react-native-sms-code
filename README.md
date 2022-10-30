# react-native-sms-code
The purpose of this package is provide SMS verification codes on Android devices.
## Installation

```sh
npm install react-native-sms-code
yarn add react-native-sms-code
```

## Usage

```js
import {
registerBroadcastReceiver,
codeReceived,
unregisterBroadcastReceiver,
} from 'react-native-sms-code';


// ...
const [code, setCode] = React.useState('');

codeReceived().then((value) => {
setCode(value);
});

React.useEffect(() => {
  registerBroadcastReceiver();

  return () => {
    unregisterBroadcastReceiver();
  };
}, []);

return (
  <View style={styles.container}>
  <Text>Code:</Text>
  <TextInput
          value={code}
          onChangeText={setCode}
          underlineColorAndroid="#3333"
        />
  </View>
);

const styles = StyleSheet.create({
  container: {
  flex: 1,
  justifyContent: 'center',
  padding: 8,
  },
  box: {
  width: 60,
  height: 60,
  marginVertical: 20,
  },
  textInput: {
  width: '100%',
  },
});

```
## Video Demonstration
![]('./example/src/assets/sms-code.gif')

## Extra Parameter
codeLength() - The length of the code to be received. Default is 6.
```js

## License

MIT
