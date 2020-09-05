/*
 *  Copyright 2015 Google Inc. All Rights Reserved.
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

/**
 * @fileoverview Generators for the Turtle Blockly demo on Android.
 * @author fenichel@google.com (Rachel Fenichel)
 */
'use strict';




// Extensions to Blockly's language and JavaScript generator.
Blockly.JavaScript['turtle_move_internal'] = function(block) {
  // Generate JavaScript for moving forward or backwards.
  var value = block.getFieldValue('VALUE');
  return 'Turtle.' + block.getFieldValue('DIR') +
      '(' + value + ', \'block_id_' + block.id + '\');\n';
};
Blockly.JavaScript['turtle_turn_internal'] = function(block) {
  // Generate JavaScript for turning left or right.
  var value = block.getFieldValue('VALUE');
  return 'Turtle.' + block.getFieldValue('DIR') +
      '(' + value + ', \'block_id_' + block.id + '\');\n';
};

Blockly.JavaScript['set_leds'] = function (block) {
 var value1 = block.getFieldValue('VALUE1');
 var value2 = block.getFieldValue('VALUE2');
 var value3 = block.getFieldValue('VALUE3');

    return 'pinMode(11, OUTPUT);\npinMode(12, OUTPUT);\npinMode(13, OUTPUT);\ndigitalWrite(11,'+value1+');\ndigitalWrite(12,'+value2+');\ndigitalWrite(13,'+value3+');\n';
};

Blockly.JavaScript['dc_motor'] = function(block) {
  // Generate JavaScript for setting the width.
  var channel = parseInt(block.getFieldValue('channel'));
  var speed = parseInt(Blockly.JavaScript.valueToCode(block, 'speed',Blockly.JavaScript.ORDER_NONE) || '255');
   if( speed > 255 || speed < -255) {
     return '!!alert!!DC motor : speed should be between -255 and 255!!\n';
   }

    var code;
    Blockly.JavaScript.setups_['setup_pin_mode_4'] =  "\npinMode(4, OUTPUT);";
    Blockly.JavaScript.setups_['setup_pin_mode_2'] = "\npinMode(2, OUTPUT);";
    Blockly.JavaScript.setups_['setup_pin_mode_7'] = "\npinMode(7, OUTPUT);";
    Blockly.JavaScript.setups_['digital_write_7'] = "\ndigitalWrite(7,LOW);";
    Blockly.JavaScript.setups_['digital_write_3'] = "\ndigitalWrite(2,HIGH);";
    Blockly.JavaScript.setups_['setup_pin_mode_6'] = "\npinMode(6, OUTPUT);";

    if ( channel == 1) {
  	  if (speed >= 0 )
          code = "digitalWrite(4,LOW);\n";
  	  else
  		code = "digitalWrite(4,HIGH);\n";
      code += "analogWrite(3,"+speed+");\n";
    }
    else if(channel == 2) {
  	   if (speed >= 0 )
          code = "digitalWrite(6,LOW);\n";
  	  else
  		code = "digitalWrite(6,HIGH);\n";
      code += "analogWrite(5,"+speed+");\n";
    }
    return code;
};

Blockly.JavaScript['lcd'] = function (block) {
  var text = Blockly.JavaScript.valueToCode(block, 'text', Blockly.JavaScript.ORDER_NONE) || '255';
  var line_number = block.getFieldValue('line_number');
  var character_number = parseInt(Blockly.JavaScript.valueToCode(block, 'character_number', Blockly.JavaScript.ORDER_NONE) || '0');
  // Assemble JavaScript into code variable.
  //import lcd
  //return 'text-'+text+'line-'+line_number+'char-'+character_number+'\n';
  if( character_number < 0  || character_number > 15 ) {
    return '!!alert!!LCD : character should be between 0 to 15!!\n';
  }
  Blockly.JavaScript.setups_["%1"] = "\n lcd.begin(16, 2);";
  Blockly.JavaScript.definitions_["includelib"] = "#include <LiquidCrystal.h>";
  Blockly.JavaScript.definitions_["definelcdpins"] = "LiquidCrystal lcd(8,9,10,11,12,13);"
  var code = 'lcd.setCursor(' + character_number + ',' + line_number + ');\n';
  code = code + 'lcd.print(' + text + ');\n'
  return code;
};

Blockly.JavaScript['clear_lcd'] = function (block) {
  //  Assemble JavaScript into code variable.
  var code = 'lcd.clear();\n';
  return code;
};

Blockly.JavaScript['bluetooth_sensor'] = function (block) {
  var baudrate = Blockly.JavaScript.valueToCode(block, 'baud', Blockly.JavaScript.ORDER_NONE) || '255';
  //define bluetooth settings
  Blockly.JavaScript.setups_['setup_bluetooth'] = "\n Serial.begin("+baudrate+");";
  Blockly.JavaScript.definitions_['define_bluetooth'] = "char readBluetooth()\n{\n while(Serial.available())\n {\n char inChar = (char)Serial.read();\n return inChar;\n}\n}\n";
    var code = 'readBluetooth()';

    return [code, Blockly.JavaScript.ORDER_NONE];
};

Blockly.JavaScript['sonar_sensor'] = function (block) {
  var value_trig = Blockly.JavaScript.valueToCode(block, 'trig', Blockly.JavaScript.ORDER_NONE);
  var value_echo = Blockly.JavaScript.valueToCode(block, 'echo', Blockly.JavaScript.ORDER_NONE);
  //define sonar settings
  Blockly.JavaScript.definitions_['define_sonar'] = "int readUltrasonic_cm(int trigPin, int echoPin)\n{ \n pinMode(trigPin, OUTPUT);\n digitalWrite(trigPin, LOW);\n delayMicroseconds(2);\n digitalWrite(trigPin, HIGH);\n delayMicroseconds(10);\n digitalWrite(trigPin, LOW);\n pinMode(echoPin, INPUT);\n return pulseIn(echoPin, HIGH)/ 29 / 2;\n}\n";
  //  Assemble Arduino into code variable.
  var code = "readUltrasonic_cm("+value_trig+","+value_echo+")";

  return [code, Blockly.JavaScript.ORDER_NONE];
};

Blockly.JavaScript['remote_sensor'] = function (block) {
  var value_tsop = Blockly.JavaScript.valueToCode(block, 'tsop', Blockly.JavaScript.ORDER_NONE);
  Blockly.JavaScript.definitions_['define_remote'] = "int remote(int pinNumber)\n{\nint value = 0;\nint time = pulseIn(pinNumber,LOW);\n if(time>2000)\n{\nfor(int counter1=0;counter1<12;counter1++)\n{\nif(pulseIn(pinNumber,LOW)>1000)\n{\nvalue = value + (1<< counter1);\n }\n}\n}\n return value;\n}\n";
  var code = "remote("+value_tsop+")";
  return [code, Blockly.JavaScript.ORDER_NONE];
};

Blockly.JavaScript['servo'] = function (block) {
  var value_channel = Blockly.JavaScript.valueToCode(block, 'channel', Blockly.JavaScript.ORDER_ATOMIC);
  var value_angle = Blockly.JavaScript.valueToCode(block, 'angle', Blockly.JavaScript.ORDER_ATOMIC);

  //define sonar settings
  Blockly.JavaScript.definitions_['define_servo_h'] = "#include <Servo.h>\n";
  Blockly.JavaScript.definitions_['define_servo_' + value_channel] = "Servo servo" + value_channel + ";\n";

  Blockly.JavaScript.setups_['define_servo' + value_channel] = '\n servo' + value_channel + '.attach('+value_channel+');\n';
  if( (value_angle < 0 ) || (value_angle > 180 )) {
     return '!!alert!!Servo : angle should be between 0 and 180!!\n';
  }
    // Assemble JavaScript into code variable.
    var code = 'servo' + value_channel + '.write(' + value_angle + ');\n';
    return code;
 };

Blockly.JavaScript['inout_tone_pin'] = function(block) {
   var value_pin = Blockly.JavaScript.valueToCode(block, "PIN", Blockly.JavaScript.ORDER_ATOMIC);
   var value_num = Blockly.JavaScript.valueToCode(block, "NUM", Blockly.JavaScript.ORDER_ATOMIC);
   Blockly.JavaScript.setups_['setup_output'+value_pin] = '\n pinMode('+value_pin+', OUTPUT);';
   var code = "tone(" + value_pin + ", " + value_num + ");\n";
   return code;
 };

Blockly.JavaScript['inout_notone_pin'] = function(block) {
   var dropdown_pin = Blockly.JavaScript.valueToCode(block, "PIN", Blockly.JavaScript.ORDER_ATOMIC);
   Blockly.JavaScript.setups_['setup_output'+dropdown_pin] = '\n pinMode('+dropdown_pin+', OUTPUT);';
   var code = "noTone(" + dropdown_pin + ");\n";
   return code;
 };

Blockly.JavaScript['inout_digital_write'] = function(block) {
   var value_pin = Blockly.JavaScript.valueToCode(block, "PIN", Blockly.JavaScript.ORDER_ATOMIC);
   var value_num = Blockly.JavaScript.valueToCode(block, "NUM", Blockly.JavaScript.ORDER_ATOMIC);
   var code = "pinMode("+value_pin+", OUTPUT);\n digitalWrite(" + value_pin + ", " + value_num + ");\n";
   return code;
 };

Blockly.JavaScript['inout_analog_write'] = function(block) {
    var value_pin = Blockly.JavaScript.valueToCode(block, "PIN", Blockly.JavaScript.ORDER_ATOMIC);
    var value_num = Blockly.JavaScript.valueToCode(block, "NUM", Blockly.JavaScript.ORDER_ATOMIC);
     if( (value_num < 0 ) || (value_num > 255 )) {
         return '!!alert!!Pin : analog value should be between 0 and 255!!\n';
      }
    var code = "pinMode("+value_pin+", OUTPUT);\n analogWrite(" + value_pin + ", " + value_num + ");\n";
    return code;
  };

Blockly.JavaScript['inout_digital_read'] = function(block) {
  var value_pin = Blockly.JavaScript.valueToCode(block, "PIN", Blockly.JavaScript.ORDER_ATOMIC);
  Blockly.JavaScript.definitions_['digital_read'] = "int digRead(int pinNumber)\n{\n pinMode("+value_pin+", INPUT);\n return digitalRead(" + value_pin + ");\n}\n"
  var code = "digRead(" + value_pin + ")";
  return [code, Blockly.JavaScript.ORDER_ATOMIC];
};

Blockly.JavaScript['inout_analog_read'] = function(block) {
  var value_pin = Blockly.JavaScript.valueToCode(block, "PIN", Blockly.JavaScript.ORDER_ATOMIC);
  Blockly.JavaScript.definitions_['analog_read'] = "int anaRead(int pinNumber)\n{\n pinMode("+value_pin+", INPUT);\n return analogRead(" + value_pin + ");\n}\n"
    var code = "anaRead(" + value_pin + ")";
    return [code, Blockly.JavaScript.ORDER_ATOMIC];
};

Blockly.JavaScript['serial_print'] = function(block) {
   var value_baud = Blockly.JavaScript.valueToCode(block, "baud", Blockly.JavaScript.ORDER_ATOMIC);
   var value_text = Blockly.JavaScript.valueToCode(block, "text", Blockly.JavaScript.ORDER_ATOMIC);
   Blockly.JavaScript.setups_['setup_serial_print'] = '\n Serial.begin('+value_baud+');';
   var code = "Serial.println("+value_text+");\n";
   return code;
 };

Blockly.JavaScript['base_pins_list'] = function() {
  var dropdown_value = this.getFieldValue('PIN');
  return [dropdown_value, Blockly.JavaScript.ORDER_ATOMIC];
};

Blockly.JavaScript['base_logic_list'] = function() {
  var dropdown_value = this.getFieldValue('LOGIC');
  return [dropdown_value, Blockly.JavaScript.ORDER_ATOMIC];
};

Blockly.JavaScript['base_delay'] = function(block) {
  var delay_time = Blockly.JavaScript.valueToCode(block, 'DELAY_TIME', Blockly.JavaScript.ORDER_ATOMIC) || '1000'
  var code = 'delay(' + delay_time + ');\n';
  return code;
};

Blockly.JavaScript['turtle_colour_internal'] = function(block) {
  // Generate JavaScript for setting the colour.
  var colour = block.getFieldValue('COLOUR');
  return 'Turtle.penColour(\'' + colour + '\', \'block_id_' +
      block.id + '\');\n';
};

Blockly.JavaScript['turtle_pen'] = function(block) {
  // Generate JavaScript for pen up/down.
  return 'Turtle.' + block.getFieldValue('PEN') +
      '(\'block_id_' + block.id + '\');\n';
};

Blockly.JavaScript['turtle_width'] = function(block) {
  // Generate JavaScript for setting the width.
  var width = Blockly.JavaScript.valueToCode(block, 'WIDTH',
      Blockly.JavaScript.ORDER_NONE) || '1';
  return 'Turtle.penWidth(' + width + ', \'block_id_' + block.id + '\');\n';
};

Blockly.JavaScript['turtle_visibility'] = function(block) {
  // Generate JavaScript for changing turtle visibility.
  return 'Turtle.' + block.getFieldValue('VISIBILITY') +
      '(\'block_id_' + block.id + '\');\n';
};

Blockly.JavaScript['turtle_print'] = function(block) {
  // Generate JavaScript for printing text.
  var argument0 = String(Blockly.JavaScript.valueToCode(block, 'TEXT',
      Blockly.JavaScript.ORDER_NONE) || '\'\'');
  return 'Turtle.drawPrint(' + argument0 + ', \'block_id_' +
      block.id + '\');\n';
};

Blockly.JavaScript['turtle_font'] = function(block) {
  // Generate JavaScript for setting the font.
  return 'Turtle.drawFont(\'' + block.getFieldValue('FONT') + '\',' +
      Number(block.getFieldValue('FONTSIZE')) + ',\'' +
      block.getFieldValue('FONTSTYLE') + '\', \'block_id_' +
      block.id + '\');\n';
};

Blockly.JavaScript['turtle_move'] = function(block) {
  // Generate JavaScript for moving forward or backwards.
  var value = Blockly.JavaScript.valueToCode(block, 'VALUE',
      Blockly.JavaScript.ORDER_NONE) || '0';
  return 'Turtle.' + block.getFieldValue('DIR') +
      '(' + value + ', \'block_id_' + block.id + '\');\n';
};

Blockly.JavaScript['turtle_turn'] = function(block) {
  // Generate JavaScript for turning left or right.
  var value = Blockly.JavaScript.valueToCode(block, 'VALUE',
      Blockly.JavaScript.ORDER_NONE) || '0';
  return 'Turtle.' + block.getFieldValue('DIR') +
      '(' + value + ', \'block_id_' + block.id + '\');\n';
};

Blockly.JavaScript['turtle_width'] = function(block) {
  // Generate JavaScript for setting the width.
  var width = Blockly.JavaScript.valueToCode(block, 'WIDTH',
      Blockly.JavaScript.ORDER_NONE) || '1';
  return 'Turtle.penWidth(' + width + ', \'block_id_' + block.id + '\');\n';
};

Blockly.JavaScript['turtle_colour'] = function(block) {
  // Generate JavaScript for setting the colour.
  var colour = Blockly.JavaScript.valueToCode(block, 'COLOUR',
      Blockly.JavaScript.ORDER_NONE) || '\'#000000\'';
     Blockly.JavaScript.definitions_["%1"] = "var hello var hi;"
     Blockly.JavaScript.definitions_["%2"] = "iiiiiiiiiiiiiii"
  return 'Turtle.penColour(' + colour + ', \'block_id_' +
      block.id + '\');\n';
};

Blockly.JavaScript['turtle_repeat_internal'] = Blockly.JavaScript['controls_repeat'];

Blockly.JavaScript['turtle_setup_loop'] = Blockly.JavaScript['setup_loop'];



/**
 * The generated code for turtle blocks includes block ID strings.  These are useful for
 * highlighting the currently running block, but that behaviour is not supported in Android Blockly
 * as of May 2016.  This snippet generates the block code normally, then strips out the block IDs
 * for readability when displaying the code to the user.
 *
 * Post-processing the block code in this way allows us to use the same generators for the Android
 * and web versions of the turtle.
 */
Blockly.JavaScript.workspaceToCodeWithId = Blockly.JavaScript.workspaceToCode;

Blockly.JavaScript.workspaceToCode = function(workspace) {
  var code = this.workspaceToCodeWithId(workspace);
  // Strip out block IDs for readability.
  code = goog.string.trimRight(code.replace(/(,\s*)?'block_id_[^']+'\)/g, ')'))
  return code;
};
