import React, {useEffect, useReducer, useState} from 'react';
import {StyleSheet, Text, TouchableHighlight} from 'react-native';

const reducer = (state: number, action: string) => {
  switch (action) {
    case 'add':
      return state + 1;
    case 'sub':
      return state - 1;
    case 'mul':
      return state * 2;
    default:
      return state;
  }
};

const UseHookDemo = () => {
  const initCount = 0;
  const [count, setCount] = useState(initCount);

  const [count1, dispatch] = useReducer(reducer, 0);

  useEffect(() => {
    console.log('mount');
    return () => {
      console.log('ummount');
    };
  }, []);

  const handleClick = () => {
    for (let i = 0; i < 3; i++) {
      //解决数据异步问题
      setCount(prevData => prevData + 1);
      // setCount(count + 1);
    }
  };

  return (
    <TouchableHighlight style={styles.wrapper} onPress={() => dispatch('add')}>
      <Text style={styles.text}>{count1}</Text>
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

export default UseHookDemo;
