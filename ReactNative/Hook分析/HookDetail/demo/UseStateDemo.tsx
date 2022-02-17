import React, {useState} from 'react';
import {StyleSheet, Text, TouchableHighlight} from 'react-native';
const UseStateDemo = () => {
  const initCount = 0;
  const [count, setCount] = useState(initCount);

  const handleClick = () => {
    for (let i = 0; i < 3; i++) {
      //解决数据异步问题
      setCount(prevData => prevData + 1);
      // setCount(count + 1);
    }
  };

  return (
    <TouchableHighlight style={styles.wrapper} onPress={handleClick}>
      <Text style={styles.text}>{count}</Text>
    </TouchableHighlight>
  );
};

const styles = StyleSheet.create({
  wrapper: {
    top: 100,
    left: 100,
    width: 100,
    height: 100,
    backgroundColor: '#ffff00',
    justifyContent: 'center', //text文字居中显示
  },
  text: {
    textAlign: 'center', //text文字居中显示
  },
});

export default UseStateDemo;
