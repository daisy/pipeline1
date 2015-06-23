#!/usr/bin/perl -w

#**************************************************************#
sub currency_expansion {

	if (
		$type{ $curr }	=~	/CURRENCY/
		&&
		$ort{ $curr }	=~	/^(?:\$|dollars?|\£|pounds?|\€|euros?)/i
	) {
	

#		print "\n
#		-----------------
#		Currency expansion:
#		P4: $ort{ $prev_4}	$type{ $prev_4}
#		P3: $ort{ $prev_3}	$type{ $prev_3}
#		P2: $ort{ $prev_2}	$type{ $prev_2}
#		P1: $ort{ $prev_1}	$type{ $prev_1}
#		P0: $ort{ $curr}	$type{ $curr }
#		N1: $ort{ $next_1}	$type{ $next_1}
#		N2: $ort{ $next_2}	$type{ $next_2}
#		N3: $ort{ $next_3}	$type{ $next_3}
#		N4: $ort{ $next_4}	$type{ $next_4}
#		\n";

		# Expand $ 10.80 to "10 dollars and 80 cents".
		if ( 
			exists ( $ort{ $next_4 } )
			&&
			$type{ $next_1 }	=~	/CURRENCY/
			&&
			$type{ $next_2 }	=~	/CURRENCY/
			&&
			$type{ $next_3 }	=~	/CURRENCY/
			&&
			$type{ $next_4 }	=~	/CURRENCY/
		) {
			
			# Empty expansion of currency word field.
			$exp{ $curr }			=	"<NONE>";
			$transcription{ $curr }		=	"<NONE>";
				
				
			# Get type of currency
			my ($currency_type,$currency_transcr,$currency_type_2,$currency_transcr_2) = &currency_type($ort{ $curr },$ort{ $next_2});

				
			# Add words and transcriptions to (sometimes already expanded) fields.
			$exp{ $next_3 }			=	"$currency_type and";
			$transcription{ $next_3 }	=	"$currency_transcr | 'ä3: n d";
						
			$exp{ $next_4 }			.=	" $currency_type_2";
			$transcription{ $next_4 }	.=	" | $currency_transcr_2";
									


		# Expand $10.80 to "10 dollars and 80 cents".
		} elsif (
			exists ( $ort{ $next_3 })
			&&
			$type{ $next_1 }	=~	/CURRENCY/
			&&
			$type{ $next_2 }	=~	/CURRENCY/
			&&
			$type{ $next_3 }	=~	/CURRENCY/
		) {
			
			# Empty expansion of currency word field.
			$exp{ $curr }			=	"<NONE>";
			$transcription{ $curr }		=	"<NONE>";

				
			# Get type of currency
			my ($currency_type,$currency_transcr,$currency_type_2,$currency_transcr_2) = &currency_type($ort{ $curr },$ort{ $next_1});

				
			# Add words and transcriptions to (sometimes already expanded) fields.
			$exp{ $next_2 }			=	"$currency_type and";
			$transcription{ $next_2 }	=	"$currency_transcr | 'ä3: n d";
						
			$exp{ $next_3 }			.=	" $currency_type_2";
			$transcription{ $next_3 }	.=	" | $currency_transcr_2";
		

			
		# Expand $ 10 to "10 dollars".
		} elsif (
			exists ( $ort{ $next_2 })
			&&
			$type{ $next_1 }	=~	/CURRENCY/
			&&
			$type{ $next_2 }	=~	/CURRENCY/
		) {
			
			# Empty expansion of currency word field.
			$exp{ $curr }			=	"<NONE>";
			$transcription{ $curr }		=	"<NONE>";

			# Get type of currency
			my ($currency_type,$currency_transcr,$currency_type_2,$currency_transcr_2) = &currency_type($ort{ $curr },$ort{ $next_2});

				
			# Add words and transcriptions to (sometimes already expanded) fields.
			$exp{ $next_2 }			.=	" $currency_type";
			$transcription{ $next_2 }	.=	" | $currency_transcr";
								

										
		# Expand $10 to "10 dollars".
		} elsif (
			exists ( $ort{ $next_1 })
			&&
			$type{ $next_1 }	=~	/CURRENCY/
		) {
			
			# Empty expansion of currency word field.
			$exp{ $curr }			=	"<NONE>";
			$transcription{ $curr }		=	"<NONE>";

			# Get type of currency
			my ($currency_type,$currency_transcr,$currency_type_2,$currency_transcr_2) = &currency_type($ort{ $curr },$ort{ $next_1});

			# Add words and transcriptions to (sometimes already expanded) fields.
			$exp{ $next_1 }			.=	" $currency_type";
			$transcription{ $next_1 }	.=	" | $currency_transcr";
								

		# Expand 10.80 $ to "10 dollars and 80 cents".
		} elsif ( 
			exists ( $ort{ $prev_4 } )
			&&
			$type{ $prev_4 }	=~	/CURRENCY/
			&&
			$type{ $prev_3 }	=~	/CURRENCY/
			&&
			$type{ $prev_2 }	=~	/CURRENCY/
			&&
			$type{ $prev_1 }	=~	/CURRENCY/
		) {
			
			
			# Empty expansion of currency word field.
			$exp{ $curr }			=	"<NONE>";
			$transcription{ $curr }		=	"<NONE>";

				
			# Get type of currency
			my ($currency_type,$currency_transcr,$currency_type_2,$currency_transcr_2) = &currency_type($ort{ $curr },$ort{ $prev_4});

				
			# Add words and transcriptions to (sometimes already expanded) fields.
			$exp{ $prev_3 }			=	"$currency_type and";
			$transcription{ $prev_3 }	=	"$currency_transcr | 'ä3: n d";
						
			$exp{ $prev_2 }			.=	" $currency_type_2";
			$transcription{ $prev_2 }	.=	" | $currency_transcr_2";


		# Expand 10.80$ to "10 dollars and 80 cents".
		} elsif ( 
			exists ( $ort{ $prev_3 } )
			&&
			$type{ $prev_3 }	=~	/CURRENCY/
			&&
			$type{ $prev_2 }	=~	/CURRENCY/
			&&
			$type{ $prev_1 }	=~	/CURRENCY/
		) {
			
			
			# Empty expansion of currency word field.
			$exp{ $curr }			=	"<NONE>";
			$transcription{ $curr }		=	"<NONE>";

				
			# Get type of currency
			my ($currency_type,$currency_transcr,$currency_type_2,$currency_transcr_2) = &currency_type($ort{ $curr },$ort{ $prev_3});

				
			# Add words and transcriptions to (sometimes already expanded) fields.
			$exp{ $prev_2 }			=	"$currency_type and";
			$transcription{ $prev_3 }	=	"$currency_transcr | 'ä3: n d";
						
			$exp{ $prev_1 }			.=	" $currency_type_2";
			$transcription{ $prev_2 }	.=	" | $currency_transcr_2";


		# Expand 10 $ to "10 dollars".
		} elsif ( 
			exists ( $ort{ $prev_2 } )
			&&
			$type{ $prev_2 }	=~	/CURRENCY/
			&&
			$type{ $prev_1 }	=~	/CURRENCY/
		) {
			
			
			# Empty expansion of currency word field.
			$exp{ $curr }			=	"<NONE>";
			$transcription{ $curr }		=	"<NONE>";

				
			# Get type of currency
			my ($currency_type,$currency_transcr,$currency_type_2,$currency_transcr_2) = &currency_type($ort{ $curr },$ort{ $prev_2});

				
			# Add words and transcriptions to (sometimes already expanded) fields.
			$exp{ $prev_2 }			.=	" $currency_type";
			$transcription{ $prev_2 }	.=	" | $currency_transcr";

						
		# Expand 10$ to "10 dollars".
		} elsif ( 
			exists ( $ort{ $prev_1 } )
			&&
			$type{ $prev_1 }	=~	/CURRENCY/
		) {
			
			
			# Empty expansion of currency word field.
			$exp{ $curr }			=	"<NONE>";
			$transcription{ $curr }		=	"<NONE>";
				
				
			# Get type of currency
			my ($currency_type,$currency_transcr,$currency_type_2,$currency_transcr_2) = &currency_type($ort{ $curr },$ort{ $prev_1});

				
			# Add words and transcriptions to (sometimes already expanded) fields.
			$exp{ $prev_1 }			.=	" $currency_type";
			$transcription{ $prev_1 }	.=	" | $currency_transcr";
				
		} # end exists
	
	} # end if CURRENCY
		
}
#************************************************************#
# currency_type
#************************************************************#
sub currency_type {
	
	my ($ort,$numerus_check) = @_;

	my ($currency_type,$currency_transc,$currency_type_2,$currency_transcr_2);
	
	if ($ort	=~	/^(?:\$|dollars?)$/i ) {
		if ( $numerus_check	eq	"1" ) {
			$currency_type		=	"dollar";
			$currency_transcr	=	"d 'å l ë r";
			$currency_type_2	=	"cents";
			$currency_transcr_2	=	"s 'e n t s";
			
		} else {
			$currency_type		=	"dollars";
			$currency_transcr	=	"d 'å l ë r s";
			$currency_type_2	=	"cents";
			$currency_transcr_2	=	"s 'e n t s";
		}
				
	} elsif ($ort	=~	/^(?:\£|pounds?)$/i ) {
		if ( $numerus_check	eq	"1" ) {
			$currency_type		=	"pound";
			$currency_transcr	=	"p 'au n d";
			$currency_type_2	=	"pence";
			$currency_transcr_2	=	"p 'e n s";
		} else {
			$currency_type		=	"pounds";
			$currency_transcr	=	"p 'au n d s";
			$currency_type_2	=	"pence";
			$currency_transcr_2	=	"p 'e n s";
		}
						
	} elsif ($ort	=~	/^(?:\€|euros?)$/i ) {
		if ( $numerus_check	eq	"1" ) {
			$currency_type		=	"euro";
			$currency_transcr	=	"j 'u4: r öw";
			$currency_type_2	=	"cents";
			$currency_transcr_2	=	"s 'e n: t s";
		} else {
			$currency_type		=	"euros";
			$currency_transcr	=	"j 'u4: r öw s";
			$currency_type_2	=	"cents";
			$currency_transcr_2	=	"s 'e n: t s";
		}

	}

	
	return ($currency_type,$currency_transcr,$currency_type_2,$currency_transcr_2);
}
#**************************************************************#
1;
