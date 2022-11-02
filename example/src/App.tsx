import * as React from 'react';
import { StyleSheet, View, Text, TextInput } from 'react-native';
import {
  registerBroadcastReceiver,
  codeReceived,
  unregisterBroadcastReceiver,
} from 'react-native-sms-code';

export default function App() {
  const [code, setCode] = React.useState('');

  const handleCodeReceived = async () => {
    try {
      const otpCode = await codeReceived();
      setCode(otpCode);
    } catch (e) {
      console.log('error', e);
    }
  };

  React.useEffect(() => {
    registerBroadcastReceiver();

    handleCodeReceived();

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
}

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
