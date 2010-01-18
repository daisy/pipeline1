FasdUAS 1.101.10   ��   ��    k             l     ��  ��    . ( Customize the DAISY Pipeline Disk Image     � 	 	 P   C u s t o m i z e   t h e   D A I S Y   P i p e l i n e   D i s k   I m a g e   
  
 l     ��  ��    J D This script has been largely inspired from a script used for AdiumX     �   �   T h i s   s c r i p t   h a s   b e e n   l a r g e l y   i n s p i r e d   f r o m   a   s c r i p t   u s e d   f o r   A d i u m X      l     ��  ��    7 1 see http://trac.adiumx.com/browser/trunk/Release     �   b   s e e   h t t p : / / t r a c . a d i u m x . c o m / b r o w s e r / t r u n k / R e l e a s e      i         I     ������
�� .aevtoappnull  �   � ****��  ��    O    �    k   �       O   y    k   x      !   I   ������
�� .aevtodocnull  �    alis��  ��   !  " # " l   ��������  ��  ��   #  $ % $ r     & ' & m    ���� 
 ' o      ���� 0 
thexorigin 
theXOrigin %  ( ) ( r     * + * m    ���� < + o      ���� 0 
theyorigin 
theYOrigin )  , - , r     . / . m    ����� / o      ���� 0 thewidth theWidth -  0 1 0 r      2 3 2 m    ����� 3 o      ���� 0 	theheight 	theHeight 1  4 5 4 l  ! !��������  ��  ��   5  6 7 6 r   ! & 8 9 8 l  ! $ :���� : [   ! $ ; < ; o   ! "���� 0 
thexorigin 
theXOrigin < o   " #���� 0 thewidth theWidth��  ��   9 o      ���� "0 thebottomrightx theBottomRightX 7  = > = r   ' , ? @ ? l  ' * A���� A [   ' * B C B o   ' (���� 0 
theyorigin 
theYOrigin C o   ( )���� 0 	theheight 	theHeight��  ��   @ o      ���� "0 thebottomrighty theBottomRightY >  D E D r   - 0 F G F m   - . H H � I I F " / V o l u m e s / D A I S Y   P i p e l i n e / . D S _ S t o r e " G o      ���� 0 dsstore dsStore E  J K J l  1 1��������  ��  ��   K  L M L O   1 S N O N k   9 R P P  Q R Q r   9 B S T S m   9 <��
�� ecvwicnv T 1   < A��
�� 
pvew R  U V U r   C J W X W m   C D��
�� boovfals X 1   D I��
�� 
tbvi V  Y�� Y r   K R Z [ Z m   K L��
�� boovfals [ 1   L Q��
�� 
stvi��   O 1   1 6��
�� 
cwnd M  \ ] \ l  T T��������  ��  ��   ]  ^ _ ^ r   T a ` a ` l  T ] b���� b n   T ] c d c m   Y ]��
�� 
icop d 1   T Y��
�� 
cwnd��  ��   a o      ���� 0 opts   _  e f e O   b | g h g k   h { i i  j k j r   h q l m l m   h k���� ` m 1   k p��
�� 
lvis k  n�� n r   r { o p o m   r u��
�� earrnarr p 1   u z��
�� 
iarr��   h o   b e���� 0 opts   f  q r q r   } � s t s 4   } ��� u
�� 
file u m   � � v v � w w 4 . b a c k g r o u n d : b a c k g r o u n d . p n g t n       x y x 1   � ���
�� 
ibkg y o   � ����� 0 opts   r  z { z l  � ���������  ��  ��   {  | } | l  � ��� ~ ��   ~   Positioning     � � �    P o s i t i o n i n g }  � � � r   � � � � � J   � � � �  � � � m   � ������ �  ��� � m   � ����� ���   � n       � � � 1   � ���
�� 
posn � 4   � ��� �
�� 
cobj � m   � � � � � � �  A p p l i c a t i o n s �  � � � r   � � � � � J   � � � �  � � � m   � ����� � �  ��� � m   � �����r��   � n       � � � 1   � ���
�� 
posn � 4   � ��� �
�� 
cobj � m   � � � � � � �  R e a d M e . r t f �  � � � r   � � � � � J   � � � �  � � � m   � ������ �  ��� � m   � �����r��   � n       � � � 1   � ���
�� 
posn � 4   � ��� �
�� 
cobj � m   � � � � � � �  I N F O �  � � � r   � � � � � J   � � � �  � � � m   � ����� � �  ��� � m   � ����� ���   � n       � � � 1   � ���
�� 
posn � 4   � ��� �
�� 
cobj � m   � � � � � � � $ D A I S Y   P i p e l i n e . a p p �  � � � r   � � � � � m   � ���
�� boovtrue � n       � � � 1   � ���
�� 
hidx � 4   � ��� �
�� 
cobj � m   � � � � � � �  R E A D M E . r t f �  � � � l  � ���������  ��  ��   �  � � � Z   �, � ����� � I  ��� ���
�� .coredoexbool        obj  � 4   � ��� �
�� 
cobj � m   � � � � � � � & E x t e r n a l   T o o l s . m p k g��   � k  ( � �  � � � r   � � � J   � �  � � � m  ����, �  ��� � m  
����,��   � n       � � � 1  ��
�� 
posn � 4  �� �
�� 
cobj � m   � � � � � & E x t e r n a l   T o o l s . m p k g �  ��� � r  ( � � � m  ��
�� boovtrue � n       � � � 1  #'��
�� 
hidx � 4  #�� �
�� 
cobj � m  " � � � � � & E x t e r n a l   T o o l s . m p k g��  ��  ��   �  � � � l --��������  ��  ��   �  � � � l --��������  ��  ��   �  � � � l --��������  ��  ��   �  � � � O  -l � � � k  5k � �  � � � r  5< � � � m  56��
�� boovtrue � 1  6;��
�� 
tbvi �  � � � r  =D � � � m  =>����   � 1  >C��
�� 
sbwi �  � � � J  EY � �  � � � \  EJ � � � o  EF���� 0 
thexorigin 
theXOrigin � m  FI����  �  � � � o  JK���� 0 
theyorigin 
theYOrigin �  � � � [  KP � � � o  KL���� "0 thebottomrightx theBottomRightX � m  LO����  �  ��� � [  PU �  � o  PQ���� "0 thebottomrighty theBottomRightY  m  QT���� $��   �  r  Zc l Z]��~ 1  Z]�}
�} 
rslt�  �~   1  ]b�|
�| 
pbnd �{ r  dk m  de�z
�z boovfals 1  ej�y
�y 
tbvi�{   � 1  -2�x
�x 
cwnd � 	
	 l mm�w�v�u�w  �v  �u  
  I mv�t�s
�t .fndrfupdnull���     obj �s   �r�q
�r 
reg? m  qr�p
�p boovfals�q    l ww�o�n�m�o  �n  �m   �l l ww�k�j�i�k  �j  �i  �l    4    �h
�h 
cdis l   �g�f m     �  D A I S Y   P i p e l i n e�g  �f     l zz�e�d�c�e  �d  �c    l zz�b�b   ; 5give the finder some time to write the .DS_Store file    � j g i v e   t h e   f i n d e r   s o m e   t i m e   t o   w r i t e   t h e   . D S _ S t o r e   f i l e  r  z  m  z{�a�a    o      �`�` 0 waittime waitTime !"! r  ��#$# m  ���_
�_ boovfals$ o      �^�^ 0 ejectme ejectMe" %&% V  ��'(' k  ��)) *+* I ���],�\
�] .sysodelanull��� ��� nmbr, m  ���[�[ �\  + -.- r  ��/0/ [  ��121 o  ���Z�Z 0 waittime waitTime2 m  ���Y�Y 0 o      �X�X 0 waittime waitTime. 343 l ���W�V�U�W  �V  �U  4 5�T5 Z ��67�S�R6 =  ��898 l ��:�Q�P: I ���O;�N
�O .sysoexecTEXT���     TEXT; b  ��<=< b  ��>?> m  ��@@ �AA  [   - f    ? o  ���M�M 0 dsstore dsStore= m  ��BB �CC    ] ;   e c h o   $ ?�N  �Q  �P  9 m  ��DD �EE  07 r  ��FGF m  ���L
�L boovtrueG o      �K�K 0 ejectme ejectMe�S  �R  �T  ( = ��HIH o  ���J�J 0 ejectme ejectMeI m  ���I
�I boovfals& J�HJ I ���GK�F
�G .ascrcmnt****      � ****K b  ��LML b  ��NON m  ��PP �QQ  w a i t e d  O o  ���E�E 0 waittime waitTimeM m  ��RR �SS J   s e c o n d s   f o r   . D S _ S T O R E   t o   b e   c r e a t e d .�F  �H    m     TT�                                                                                  MACS   alis    r  Macintosh HD               Ď��H+     �
Finder.app                                                       s��ql�        ����  	                CoreServices    Ď��      �q^�       �   Q   P  3Macintosh HD:System:Library:CoreServices:Finder.app    
 F i n d e r . a p p    M a c i n t o s h   H D  &System/Library/CoreServices/Finder.app  / ��    UVU l     �D�C�B�D  �C  �B  V W�AW l     �@�?�>�@  �?  �>  �A       �=XY�<�;�:�9�8�7 HZ�6�5�4�3�2�1�0�=  X �/�.�-�,�+�*�)�(�'�&�%�$�#�"�!� 
�/ .aevtoappnull  �   � ****�. 0 
thexorigin 
theXOrigin�- 0 
theyorigin 
theYOrigin�, 0 thewidth theWidth�+ 0 	theheight 	theHeight�* "0 thebottomrightx theBottomRightX�) "0 thebottomrighty theBottomRightY�( 0 dsstore dsStore�' 0 opts  �& 0 waittime waitTime�% 0 ejectme ejectMe�$  �#  �"  �!  �   Y � ��[\�
� .aevtoappnull  �   � ****�  �  [  \ BT������������ H������
�	������ v��� �� ������� � � � ��� ����� � �������������������������@B��DPR��
� 
cdis
� .aevtodocnull  �    alis� 
� 0 
thexorigin 
theXOrigin� <� 0 
theyorigin 
theYOrigin��� 0 thewidth theWidth��� 0 	theheight 	theHeight� "0 thebottomrightx theBottomRightX� "0 thebottomrighty theBottomRightY� 0 dsstore dsStore
� 
cwnd
� ecvwicnv
� 
pvew
� 
tbvi
�
 
stvi
�	 
icop� 0 opts  � `
� 
lvis
� earrnarr
� 
iarr
� 
file
� 
ibkg���  �
�� 
cobj
�� 
posn�� ���r
�� 
hidx
�� .coredoexbool        obj ��,
�� 
sbwi�� �� �� $�� 
�� 
rslt
�� 
pbnd
�� 
reg?
�� .fndrfupdnull���     obj �� 0 waittime waitTime�� 0 ejectme ejectMe
�� .sysodelanull��� ��� nmbr
�� .sysoexecTEXT���     TEXT
�� .ascrcmnt****      � ****����*��/o*j O�E�O�E�O�E�O�E�O��E�O��E�O�E�O*a , a *a ,FOf*a ,FOf*a ,FUO*a ,a ,E` O_  a *a ,FOa *a ,FUO*a a /_ a ,FOa a lv*a  a !/a ",FOa #a $lv*a  a %/a ",FOa a $lv*a  a &/a ",FOa #a lv*a  a '/a ",FOe*a  a (/a ),FO*a  a */j + )a ,a ,lv*a  a -/a ",FOe*a  a ./a ),FY hO*a , 8e*a ,FOj*a /,FO�a 0��a 1�a 2a 3vO_ 4*a 5,FOf*a ,FUO*a 6fl 7OPUOjE` 8OfE` 9O ;h_ 9f kj :O_ 8kE` 8Oa ;�%a <%j =a >  
eE` 9Y h[OY��Oa ?_ 8%a @%j AU�< 
�; <�:��9��8��70Z ]] ^��^ T������
�� 
brow�� 9
�� kfrmID  
�� 
icop�6 
�5 boovtrue�4  �3  �2  �1  �0   ascr  ��ޭ