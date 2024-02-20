int leftPin = A0;
int rightPin = A1;
int leftValue = 0;
int rightValue = 0;

const long minPressDelay = 100;

long lastPressedLeft = 0;
long lastPressedRight = 0;

bool leftPressed = false;
bool rightPressed = false;

void setup() {
  Serial.begin(9600);
}

void loop() {
  
  leftValue = analogRead(leftPin);
  rightValue = analogRead(rightPin);
  
  if(leftValue < 50){
    if(lastPressedLeft + minPressDelay < millis()){
      if(!leftPressed){
        Serial.println("+Left");
        leftPressed = true;
      }
    }
  }else{
    if(leftPressed){
      Serial.println("-Left");
      leftPressed = false;
      lastPressedLeft = millis();
    }
  }
  
  if(rightValue < 50){
    if(lastPressedRight + minPressDelay < millis()){
      if(!rightPressed){
        Serial.println("+Right");
        rightPressed = true;
      }
    }
  }else{
    if(rightPressed){
      Serial.println("-Right");
      rightPressed = false;
      lastPressedRight = millis();
    }
  }
  
  delay(1);
}
