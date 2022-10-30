import { NativeEventEmitter, NativeModules, Platform } from 'react-native';

const LINKING_ERROR =
  `The package 'react-native-sms-code' doesn't seem to be linked. Make sure: \n\n` +
  Platform.select({ ios: "- You have run 'pod install'\n", default: '' }) +
  '- You rebuilt the app after installing the package\n' +
  '- You are not using Expo Go\n';

const SmsCode = NativeModules.SmsCode
  ? NativeModules.SmsCode
  : new Proxy(
      {},
      {
        get() {
          throw new Error(LINKING_ERROR);
        },
      }
    );

const isAndroid = Platform.OS === 'android';

const eventEmitter = isAndroid ? new NativeEventEmitter(SmsCode) : null;

export function registerBroadcastReceiver(): void {
  isAndroid ? SmsCode.registerBroadcastReceiver() : null;
}

export const codeReceived = (): Promise<string> => {
  return isAndroid
    ? new Promise((resolve, _) => {
        const subscription = eventEmitter?.addListener(
          'code',
          (event: string) => {
            resolve(event);
            subscription?.remove();
          }
        );
      })
    : Promise.resolve('');
};

export const codeLength = (length: string): void =>
  isAndroid ? SmsCode.codeLength(length) : null;

export const unregisterBroadcastReceiver = () => {
  isAndroid ? SmsCode.unRegisterBroadcastService() : null;
};
