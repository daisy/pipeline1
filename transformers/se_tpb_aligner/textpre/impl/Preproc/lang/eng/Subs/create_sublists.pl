#!/usr/bin/perl -w

#***************************************************************#
#	Creates sublists with default values for each		#
#	indexed orthographic unit.				#
#								#
#***************************************************************#
sub create_sublists {
	
	my $number_of_components = shift;
	
	# Empty lists
	%dependency = ();
	%emphasis = ();
	%exp = ();
	%homo_across = ();
	%homo_within = ();
	%lang = ();
	%morphology = ();
	%orthography = ();
	%pos = ();
	%parse = ();
	%parse_number = ();
	%phrase_depth = ();
	%pause = ();
	%rate = ();
	%transcription = ();
	%voice = ();
	%type = ();
		
	
	my $counter = 0;
	my $pcounter = 0;
	until ($counter == $number_of_components + 1) {
		
		$pcounter = &format_pcounter($counter);
				
		$dependency{$pcounter}		=	$default_dependency;
		$emphasis{$pcounter}		=	$default_emphasis;
		$exp{$pcounter}			=	$default_expansion;
		$homo_across{$pcounter}		=	$default_homo_across;
		$homo_within{$pcounter}		=	$default_homo_within;
		$lang{$pcounter}		=	$default_lang;
		
		$orthography{$pcounter}		=	$orthography[$counter];
		
		$morphology{$pcounter}		=	$default_morphology;
		$pos{$pcounter}			=	$default_pos;
		
		$parse{$pcounter}		=	$default_parse;
		$parse_number{$pcounter}	=	$default_parse_number;
		$phrase_depth{$pcounter}	=	$default_phrase_depth;
		$pause{$pcounter}		=	$default_pause;
		$rate{$pcounter}		=	$default_rate;
		$transcription{$pcounter}	=	$default_transcription;
		$voice{$pcounter}		=	$default_voice;
		$type{$pcounter}		=	$default_type;
		
		$counter++;
	}
}
#***************************************************************#
1;




